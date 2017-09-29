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
import edu.nju.memo.domain.MemoItem
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
            // take the first and wrap it as an MemoItem, set following as its attachments
            wrapMemoItem(getAttachments(data)).reduceAttachments().cacheToTemp()

    /* create attachments from ClipData.Items */
    private fun getAttachments(data: ClipData) =
            data.description.sequence(data.itemCount, ClipDescription::getMimeType).
                    zip(data.sequence(data.itemCount, ClipData::getItemAt)).
                    map { (type, item) -> chooseClipParser(type)(item) }.
                    toMutableList()

    private fun vendorHack(item: MemoItem, extra: Bundle): MemoItem {
        if (FROM_CHROME in extra.keySet())
            item.attachments[0].let {
                it.type = "image/*"
                it.cacheState = NOT_CACHED
            }
        return item
    }

    private fun MemoItem.cacheToTemp() = also { AttachmentFileCache.cacheToTemp(it.attachments) }

    private fun MemoItem.reduceAttachments() = this.
            takeIf { it.trimmedAttachments().size >= 1 }?.
            takeIf { canMerge(it, it.attachments[0]) }?.
            apply { content = attachments.removeAt(0).content } ?: this

    private fun wrapMemoItem(attachments: MutableList<Attachment>) =
            attachments.
                    takeIf { attachments.isNotEmpty() }?.
                    let {
                        (if (it[0].type == "text/plain") MemoItem(null, it.removeAt(0).content) else MemoItem())
                                .apply { this.attachments = it }
                    } ?: MemoItem()

    private fun canMerge(item: MemoItem, attachment: Attachment) =
            // attachment's uri is null and attachment's type is text/*
            // item's content is empty or equals attachment's content
            attachment.uri == null && attachment.type.startsWith("text/")
                    && (item.content.isEmpty() || item.content == attachment.content)

    private fun chooseIntentParser(type: String) = intentParsers.getValue(type.split('/')[0])

    private fun chooseClipParser(type: String) = clipDataParsers[type] ?: clipDataParsers.getValue(type.split('/')[0])

    private val intentParsers = mapOf<String, (Intent) -> MemoItem>(
            "text" to ::text,
            "image" to ::image
    ).withDefault { { _: Intent -> MemoItem() } }

    private val clipDataParsers = mapOf<String, (ClipData.Item) -> Attachment>(
            ClipDescription.MIMETYPE_TEXT_URILIST to ::textUriList,
            ClipDescription.MIMETYPE_TEXT_HTML to ::textHtml,
            ClipDescription.MIMETYPE_TEXT_PLAIN to ::textPlain,
            ClipDescription.MIMETYPE_TEXT_INTENT to ::textIntentURI,
            "image" to ::image
    ).withDefault { { _: ClipData.Item -> Attachment() } }

    private val FROM_CHROME = "org.chromium.chrome.extra.TASK_ID"
}