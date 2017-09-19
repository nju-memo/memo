package edu.nju.memo.core.parser

import android.content.Context
import android.content.Intent
import edu.nju.memo.domain.MemoItem

/**
 * Created by tinker on 2017/9/19.
 */
class FallbackIntentParser : MimeIntentParser {
    override fun canParse(mimeType: String): Boolean = true

    override fun parseIntent(intent: Intent, context: Context) = MemoItem()

}