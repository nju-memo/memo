package edu.nju.memo.core.parser.clipdata

import android.content.ClipData
import edu.nju.memo.common.safeToString
import edu.nju.memo.domain.Attachment
import edu.nju.memo.domain.NOT_CACHED

/**
 * @author [Cleveland Alto](mailto:tinker19981@hotmail.com)
 */

fun image(item: ClipData.Item) = Attachment(item.uri, item.text.safeToString(), "image/*")
        .apply { if (item.uri.host == "NOT CACHED") cacheState = NOT_CACHED }