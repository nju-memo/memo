package edu.nju.memo.core.parser.intent

import android.content.Intent
import edu.nju.memo.common.extra
import edu.nju.memo.domain.MemoItem

/**
 * Parser of MemoItem for text/`*`
 */

/**
 * text/plain
 * */
fun textPlain(intent: Intent) = MemoItem(intent.extra(Intent.EXTRA_SUBJECT), intent.extra(Intent.EXTRA_TEXT), "text/plain")

