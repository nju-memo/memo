package edu.nju.memo.core.parser

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.os.Bundle
import edu.nju.memo.common.sequence
import edu.nju.memo.core.MemoItemFactory
import edu.nju.memo.core.parser.clipdata.image
import edu.nju.memo.core.parser.clipdata.textHtml
import edu.nju.memo.core.parser.clipdata.textIntentURI
import edu.nju.memo.core.parser.clipdata.textPlain
import edu.nju.memo.core.parser.clipdata.textUriList
import edu.nju.memo.core.parser.intent.image
import edu.nju.memo.core.parser.intent.text
import edu.nju.memo.dao.AttachmentFileCache
import edu.nju.memo.domain.Attachment
import edu.nju.memo.domain.Memo
import edu.nju.memo.domain.NOT_CACHED

/**
 * Implementation of [MemoItemFactory].
 *
 * @author [Cleveland Alto](mailto:tinker19981@hotmail.com)
 */
object MemoItemFactoryImpl : MemoItemFactory {
    override fun getMemoItem(intent: Intent) =
            chooseIntentParser(intent.type)(intent).
                    apply { attachments = getAttachments(intent.clipData) }.
                    reduceAttachments().
                    let { vendorHack(it, intent.extras) }.cacheToTemp()

    override fun getMemoItem(data: ClipData) =
            // take the first and wrap it as an Memo, set following as its attachments
            wrapMemoItem(getAttachments(data)).reduceAttachments().cacheToTemp()

    /* create attachments from ClipData.Items */
    private fun getAttachments(data: ClipData) =
            data.description.sequence(data.itemCount, ClipDescription::getMimeType).
                    zip(data.sequence(data.itemCount, ClipData::getItemAt)).
                    map { (type, item) -> chooseClipParser(type)(item) }.
                    toMutableList()

    private fun vendorHack(item: Memo, extra: Bundle): Memo {
        if (FROM_CHROME in extra.keySet())
            item.attachments[0].let {
                it.uriType = "imageView/*"
                it.cacheState = NOT_CACHED
            }
        return item
    }

    private fun Memo.cacheToTemp() = also { AttachmentFileCache.cacheToTemp(it.attachments) }

    private fun Memo.reduceAttachments() = this.
            takeIf { it.trimmedAttachments().size >= 1 }?.
            takeIf { canMerge(it, it.attachments[0]) }?.
            apply { summary = attachments.removeAt(0).text } ?: this

    private fun wrapMemoItem(attachments: MutableList<Attachment>) =
            attachments.
                    takeIf { attachments.isNotEmpty() }?.
                    let {
                        (if (it[0].uriType == "textView/plain") Memo(null, it.removeAt(0).text) else Memo())
                                .apply { this.attachments = it }
                    } ?: Memo()

    private fun canMerge(item: Memo, attachment: Attachment) =
            // attachment's uri is null and attachment's uriType is textView/*
            // item's summary is empty or equals attachment's summary
            attachment.uri == null && attachment.uriType.startsWith("textView/")
                    && (item.summary.isEmpty() || item.summary == attachment.text)

    private fun chooseIntentParser(type: String) = intentParsers.getValue(type.split('/')[0])

    private fun chooseClipParser(type: String) = clipDataParsers[type] ?: clipDataParsers.getValue(type.split('/')[0])

    private val intentParsers = mapOf<String, (Intent) -> Memo>(
            "textView" to ::text,
            "imageView" to ::image
    ).withDefault { { _: Intent -> Memo() } }

    private val clipDataParsers = mapOf<String, (ClipData.Item) -> Attachment>(
            ClipDescription.MIMETYPE_TEXT_URILIST to ::textUriList,
            ClipDescription.MIMETYPE_TEXT_HTML to ::textHtml,
            ClipDescription.MIMETYPE_TEXT_PLAIN to ::textPlain,
            ClipDescription.MIMETYPE_TEXT_INTENT to ::textIntentURI,
            "imageView" to ::image
    ).withDefault { { _: ClipData.Item -> Attachment() } }

    private val FROM_CHROME = "org.chromium.chrome.extra.TASK_ID"
}