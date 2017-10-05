package edu.nju.memo.activities

import android.graphics.Bitmap
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.support.v7.widget.LinearLayoutManager
import edu.nju.memo.R
import edu.nju.memo.common.*
import edu.nju.memo.domain.Attachment
import edu.nju.memo.domain.Memo
import kotlinx.android.synthetic.main.fragment_memo_detail.*
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @author [Cleveland Alto](mailto:tinker19981@hotmail.com)
 */
class MemoDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_memo_detail)

        init()

        intent.safeExtra<Memo>("memo")?.let { renderContent(it) }
    }

    fun init() {
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
        attachmentAdapter?.disableEdit()
        edit_title.readOnly()
    }

    private fun enableEdit() {
        attachmentAdapter?.enableEdit()
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
            toolbar.background.alpha = (255 * // verticalOffset is always minus
                    (1 + verticalOffset / (appbar.totalScrollRange * 0.75)).coerceAtLeast(0.0)).toInt()
        }
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
}