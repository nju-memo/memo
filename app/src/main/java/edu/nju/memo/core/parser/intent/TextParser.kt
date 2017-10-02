package edu.nju.memo.core.parser.intent

import android.content.Intent
import edu.nju.memo.common.extra
import edu.nju.memo.domain.Memo

/**
 * Parser of Memo for text/`*`
 */

/**
 * text/plain
 * */
fun text(intent: Intent) = Memo(intent.extra(Intent.EXTRA_SUBJECT), intent.extra(Intent.EXTRA_TEXT))


