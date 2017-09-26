package edu.nju.memo.core

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import edu.nju.memo.domain.MemoItem

/**
 * Factory to build MemoItem.
 *
 * Created by tinker on 2017/9/19.
 */
interface MemoItemFactory {
    /**
     * Build a MemoItem from an Intent.
     *
     * `extras[Intent.EXTRA_SUBJECT]` -> title
     * `extras[Intent.EXTRA_TEXT]` -> content
     * `clipData` -> attachments
     *
     * @param intent an intent
     * @return corresponding MemoItem
     * */
    fun getMemoItem(intent: Intent): MemoItem

    /**
     * Build a MemoItem from a ClipData.
     *
     * Conversion is based on the mime type. See [ClipDescription] for more info.
     *
     * @param data a ClipData
     * @return corresponding MemoItem
     * */
    fun getMemoItem(data: ClipData): MemoItem
}