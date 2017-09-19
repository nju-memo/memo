package edu.nju.memo.core

import android.content.Context
import android.content.Intent
import edu.nju.memo.domain.MemoItem

/**
 * Created by tinker on 2017/9/19.
 */
interface MemoItemFactory {
    fun parseIntent(intent: Intent, context: Context): MemoItem
}