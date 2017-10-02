package edu.nju.memo.core.parser.intent

import android.content.Intent
import edu.nju.memo.common.extra
import edu.nju.memo.domain.Memo

/**
 * For image/`*`
 * */
/* the EXTRA_SUBJECT & EXTRA_TEXT entry maybe exist, so try retrieving them in case */
fun image(intent: Intent) = Memo(intent.extra(Intent.EXTRA_SUBJECT), intent.extra(Intent.EXTRA_TEXT))
