package edu.nju.memo.activities

import android.graphics.Bitmap
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.WindowManager
import edu.nju.memo.R
import edu.nju.memo.common.*
import edu.nju.memo.domain.Attachment
import edu.nju.memo.domain.Memo
import kotlinx.android.synthetic.main.layout_memo_detail.*
import org.jetbrains.anko.imageBitmap

/**
 * @author [Cleveland Alto](mailto:tinker19981@hotmail.com)
 */
class MemoDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setContentView(R.layout.layout_memo_detail)

        init()

        intent.safeExtra<Memo>("memo")?.let { renderContent(it) }
    }

    private fun init() {
        setSupportActionBar(toolbar)

        recycler_attachments.layoutManager = LinearLayoutManager(this)
    }

    private fun renderContent(memo: Memo) {
        setHeader(memo.getThemePic(), memo.title).let { themeColor ->
            setBody(memo.summary, memo.attachments, themeColor)
        }

        showFab()
        disableEdit()
    }

    private fun showFab() {
        fab_edit.show()
        fab_edit.setOnClickListener { enableEdit() }
    }

    private fun setBody(summary: String, attachments: List<Attachment>, @ColorInt themeColor: Int) {
        attachmentAdapter?.recycle()
        attachmentAdapter = AttachmentsAdapter(summary, attachments, themeColor)
        recycler_attachments.adapter = attachmentAdapter
    }

    private fun setHeader(headerPic: Bitmap?, title: String) =
            when {
                headerPic != null && title.isNotBlank() -> setDecoratedHeader(headerPic, title)
                headerPic != null && title.isBlank() -> setHeaderPicture(headerPic)
                headerPic == null && title.isBlank() -> setHeadless().let { null }
                headerPic == null && title.isNotBlank() -> setHeaderTitle(title).let { null }
                else -> null
            } ?: color(R.color.colorPrimary)

    private fun disableEdit() {
        attachmentAdapter?.editable = false
        edit_title.readOnly()
    }

    private fun enableEdit() {
        attachmentAdapter?.editable = true
        edit_title.writable()
    }

    private fun Memo.getThemePic() =
            attachments.
                    find { it.uriType.startsWith("image/") }?.
                    uri.toFile()?.takeIf { it.exists() }?.
                    let { decodeBitmap(it.path, height = dimensionPixelSize(R.dimen.appbar_height)) }


    @ColorInt
    private fun setDecoratedHeader(bitmap: Bitmap, title: String): Int {
        decorateTitleScrim()
        setHeaderTitle(title)
        return setHeaderPicture(bitmap)
    }

    private fun decorateTitleScrim() {
        toolbar.setBackgroundColor(color(R.color.black_transparent))
        layout_appbar.addOnOffsetChangedListener { appbar, verticalOffset ->
            // verticalOffset is always minus
            val multiplier = (-verticalOffset / (appbar.totalScrollRange * 0.75f)).coerceAtMost(1.0f)
            toolbar.background.alpha = (255 * (1 - multiplier)).toInt()
            button_back.alpha = multiplier
            if (-verticalOffset < appbar.totalScrollRange) button_back.setOnClickListener(null)
            else button_back.setOnClickListener(backwardListener)
        }
        layout_appbar.addOnOffsetChangedListener(TitleTranslateManager(edit_title, 45.0.toPx()))
    }

    private fun setHeaderTitle(title: String) {
        edit_title.setText(title)
    }

    @ColorInt
    private fun setHeaderPicture(bitmap: Bitmap): Int {
        view_image_header.imageBitmap = bitmap
        return Palette.from(bitmap).generate().let { palette ->
            palette.getDarkVibrantColor(color(R.color.colorPrimary)).also { themeColor ->
                alphaBlend(color(R.color.black_transparent), themeColor).let { bgColor ->
                    layout_collapsing.setStatusBarScrimColor(bgColor)
                    layout_collapsing.setContentScrimColor(bgColor)
                }
            }
        }
    }

    private fun setHeadless() {
        layout_appbar.setExpanded(false)
        layout_appbar.isEnabled = false
    }

    private var attachmentAdapter: AttachmentsAdapter? = null
    private val backwardListener = { _: View -> info("111"); this@MemoDetailActivity.finish() }
}

class TitleTranslateManager(private val view: View,
                            private val totalDistance: Int) : AppBarLayout.OnOffsetChangedListener {
    private val right by lazy { view.right }
    private val left by lazy { view.left }
    private val top by lazy { view.top }
    private val bottom by lazy { view.bottom }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        val offset = totalDistance * -verticalOffset / appBarLayout.totalScrollRange
        view.layout(left + offset, top, right, bottom)
    }

}