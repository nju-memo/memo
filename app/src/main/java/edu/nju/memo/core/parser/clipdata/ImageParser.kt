package edu.nju.memo.core.parser.clipdata

import android.content.ClipData
import edu.nju.memo.common.safeToString
import edu.nju.memo.domain.Attachment
import edu.nju.memo.domain.NOT_CACHED

/**
 * Created by tinker on 2017/9/28.
 */

fun image(item: ClipData.Item) = Attachment(item.uri, item.text.safeToString(), "image/*").apply { cacheState = NOT_CACHED }