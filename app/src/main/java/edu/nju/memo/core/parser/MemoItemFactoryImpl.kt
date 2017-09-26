package edu.nju.memo.core.parser

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.os.Bundle
import edu.nju.memo.common.iter
import edu.nju.memo.core.MemoItemFactory
import edu.nju.memo.core.parser.clipdata.textUriList
import edu.nju.memo.core.parser.intent.textPlain
import edu.nju.memo.domain.Attachment
import edu.nju.memo.domain.MemoItem

/**
 * Created by tinker on 2017/9/19.
 */
object MemoItemFactoryImpl : MemoItemFactory {
    override fun getMemoItem(intent: Intent) =
            intentParsers[intent.type]!!(intent).apply { attachments = getAttachments(intent.clipData) }
                    .let { vendorHack(it, intent.extras) }

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

    private val intentParsers = mapOf<String, (Intent) -> MemoItem>(
            "text/plain" to ::textPlain
    ).withDefault { { _: Intent -> MemoItem() } }
    private val clipDataParsers = mapOf<String, (ClipData.Item) -> Attachment>(
            ClipDescription.MIMETYPE_TEXT_URILIST to ::textUriList
    ).withDefault { { _: ClipData.Item -> Attachment() } }

    private fun wrapAttachment(attachment: Attachment) =
            MemoItem(null, attachment.content).apply { addAttachment(attachment) }
}