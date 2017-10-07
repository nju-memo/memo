package edu.nju.memo.activities

import android.content.Context
import android.graphics.Rect
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet

/**
 * @author [Cleveland Alto](mailto:tinker19981@hotmail.com)
 */
class AdjustResizeLayout(context: Context) :
        CoordinatorLayout(context) {

    private val mInsets = intArrayOf(0, 0, 0, 0)
    override fun fitSystemWindows(insets: Rect): Boolean {
        mInsets[0] = insets.left
        mInsets[1] = insets.top
        mInsets[2] = insets.right

        insets.left = 0
        insets.top = 0
        insets.right = 0
        return super.fitSystemWindows(insets)
    }
}