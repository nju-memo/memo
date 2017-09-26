package edu.nju.memo.core.parser

import android.content.Intent
import edu.nju.memo.domain.MemoItem

/**
 * Parser to extract info from intent and parse it to a MemoItem
 */
interface IntentParser {
    fun memoItem(intent: Intent): MemoItem
}