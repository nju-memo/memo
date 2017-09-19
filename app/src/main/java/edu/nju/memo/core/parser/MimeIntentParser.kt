package edu.nju.memo.core.parser

import android.content.Context
import android.content.Intent
import edu.nju.memo.domain.MemoItem

/**
 * Parser to extract info from intent and parse it to a MemoItem
 */
interface MimeIntentParser {
    fun canParse(mimeType: String): Boolean

    fun parseIntent(intent: Intent, context: Context): MemoItem
}