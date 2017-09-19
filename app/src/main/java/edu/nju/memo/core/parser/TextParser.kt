package edu.nju.memo.core.parser

import android.content.ClipData
import android.content.Context
import android.content.Intent
import edu.nju.memo.common.extra
import edu.nju.memo.common.iter
import edu.nju.memo.domain.Attachment
import edu.nju.memo.domain.MemoItem

/**
 * Parser for text/`*`
 */
class TextParser : MimeIntentParser {
    override fun canParse(mimeType: String) = mimeType.startsWith("text")

    override fun parseIntent(intent: Intent, context: Context) = intent.let {
        MemoItem(it.extra(Intent.EXTRA_TITLE), it.extra(Intent.EXTRA_TEXT)).// get item's title & content
                apply {
                    // get attachment's content & uri
                    attachment = it.clipData?.
                            iter(ClipData::getItemCount, ClipData::getItemAt)?.
                            map { Attachment(it.text?.toString(), it.uri?.toString()) }?.
                            toList() ?: emptyList()
                }
    }
}