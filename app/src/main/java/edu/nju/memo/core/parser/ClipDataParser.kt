package edu.nju.memo.core.parser

import android.content.ClipData
import edu.nju.memo.domain.Attachment
import edu.nju.memo.domain.MemoItem

/**
 * Created by tinker on 2017/9/25.
 */
interface ClipDataParser {
    fun canParse(mimeType: String): Boolean

    fun parseAsMemoItem(data: ClipData.Item): MemoItem

    fun parseAsAttachment(data: ClipData.Item): Attachment
}