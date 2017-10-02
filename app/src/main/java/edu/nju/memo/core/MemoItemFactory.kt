package edu.nju.memo.core

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import edu.nju.memo.domain.Memo

/**
 * Factory to build Memo.
 *
 * Created by tinker on 2017/9/19.
 */
interface MemoItemFactory {
    /**
     * Build a Memo from an Intent.
     *
     * `extras[Intent.EXTRA_SUBJECT]` -> title
     * `extras[Intent.EXTRA_TEXT]` -> summary
     * `clipData` -> attachments
     *
     * @param intent an intent
     * @return corresponding Memo
     * */
    fun getMemoItem(intent: Intent): Memo

    /**
     * Build a Memo from a ClipData.
     *
     * Conversion is based on the mime type. See [ClipDescription] for more info.
     *
     * @param data a ClipData
     * @return corresponding Memo
     * */
    fun getMemoItem(data: ClipData): Memo
}