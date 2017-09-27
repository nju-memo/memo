package edu.nju.memo.core.parser

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.os.Bundle
import edu.nju.memo.common.iter
import edu.nju.memo.core.MemoItemFactory
import edu.nju.memo.core.parser.clipdata.textHtml
import edu.nju.memo.core.parser.clipdata.textIntentURI
import edu.nju.memo.core.parser.clipdata.textUriList
import edu.nju.memo.core.parser.clipdata.textPlain
import edu.nju.memo.core.parser.intent.textPlain
import edu.nju.memo.domain.Attachment
import edu.nju.memo.domain.MemoItem

/**
 * Implementation of [MemoItemFactory].
 *
 * Created by tinker on 2017/9/19.
 */
object MemoItemFactoryImpl : MemoItemFactory {
    override fun getMemoItem(intent: Intent) =
            intentParsers[intent.type]!!(intent).apply { attachments = getAttachments(intent.clipData) }
                    .let { vendorHack(reduceAttachments(it), intent.extras) }

    override fun getMemoItem(data: ClipData) =
            getAttachments(data).let { wrapAttachment(it.removeAt(0)).apply { attachments = it } }

    private fun getAttachments(data: ClipData) =
            data.description.iter(data.itemCount, ClipDescription::getMimeType).
                    zip(data.iter(data.itemCount, ClipData::getItemAt)).
                    map { (type, item) -> clipDataParsers[type]!!(item) }.
                    toMutableList()

    private fun vendorHack(item: MemoItem, extra: Bundle): MemoItem {
        return item
    }

    private fun reduceAttachments(item: MemoItem) = item.
            takeIf { it.trimmedAttachments().size == 1 }?.
            takeIf { canMerge(it, it.attachments[0]) }?.
            apply { content = attachments[0].content;attachments.removeAt(0) } ?: item

    private val intentParsers = mapOf<String, (Intent) -> MemoItem>(
            "text/plain" to ::textPlain
    ).withDefault { { _: Intent -> MemoItem() } }

    private val clipDataParsers = mapOf<String, (ClipData.Item) -> Attachment>(
            ClipDescription.MIMETYPE_TEXT_URILIST to ::textUriList,
            ClipDescription.MIMETYPE_TEXT_HTML to ::textHtml,
            ClipDescription.MIMETYPE_TEXT_PLAIN to ::textPlain,
            ClipDescription.MIMETYPE_TEXT_INTENT to ::textIntentURI
    ).withDefault { { _: ClipData.Item -> Attachment() } }

    private fun wrapAttachment(attachment: Attachment) =
            MemoItem(null, attachment.content, attachment.type).apply { addAttachment(attachment) }

    private fun canMerge(item: MemoItem, attachment: Attachment) =
            attachment.uri == null && (item.content.isEmpty() || attachment.content == item.content)
}