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
import android.widget.TextView
import edu.nju.memo.R
import edu.nju.memo.common.*
import edu.nju.memo.dao.CachedMemoDao
import edu.nju.memo.domain.Attachment
import edu.nju.memo.domain.Memo
import kotlinx.android.synthetic.main.layout_memo_detail.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.imageBitmap

/**
 * @author [Cleveland Alto](mailto:tinker19981@hotmail.com)
 */
class MemoDetailActivity : AppCompatActivity() {
    private lateinit var mMemo: Memo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setContentView(R.layout.layout_memo_detail)

        init()
    }

    private fun init() {
        setSupportActionBar(toolbar)
        layout_outer.setStatusBarBackgroundColor(color(R.color.transparent))
        recycler_attachments.layoutManager = LinearLayoutManager(this)
        button_back.setOnClickListener { finish() }
        button_done.setOnClickListener { save() }

        showFab()
        fab_edit.setOnClickListener { enableEdit() }

        mMemo = intent.safeExtra<Memo>("memo") ?: Memo()
        renderContent()
    }

    private fun renderContent() {
        setHeader(mMemo.getThemePic(), mMemo.mTitle).
                let { themeColor -> doAsync { setBody(mMemo.mSummary, mMemo.mAttachments, themeColor) } }

        disableEdit()
    }

    private fun showFab() {
        fab_edit.show()
    }

    private fun hideFab() {
        fab_edit.hide()
    }

    private fun showBtnDone() {
        button_done.alpha = 1.0f
    }

    private fun hideBtnDone() {
        button_done.alpha = 0.0f
    }

    private fun setBody(summary: String, attachments: List<Attachment>, @ColorInt themeColor: Int) {
        mAttachmentAdapter?.recycle()
        mAttachmentAdapter = AttachmentsAdapter(summary, attachments, themeColor)
        recycler_attachments.adapter = mAttachmentAdapter
    }

    private fun setHeader(headerPic: Bitmap?, title: String) =
            when {
                headerPic != null && title.isNotBlank() -> setDecoratedHeader(headerPic, title)
                headerPic != null && title.isBlank() -> setHeaderPicture(headerPic)
                headerPic == null -> setHeaderTitle(title).let { null }
                else -> null
            } ?: color(R.color.colorPrimary)

    private fun save() {
        mAttachmentAdapter?.save()?.
                let { (summary, attachments) ->
                    mMemo.apply {
                        this.mTitle = edit_title.text.toString()
                        this.mSummary = summary
                        this.mAttachments = attachments.toMutableList()
                    }
                }.
                let { doAsync { CachedMemoDao.updateOrInsert(it!!) };info(it) }
        showFab()
        hideBtnDone()
        disableEdit()
    }

    private fun disableEdit() {
        mAttachmentAdapter?.editable = false
        edit_title.readOnly()
        flag = false
    }

    private var flag = false
    private fun enableEdit() {
        if (flag) return disableEdit()

        hideFab()
        showBtnDone()

        mAttachmentAdapter?.editable = true
        edit_title.writable()
        flag = true
    }

    private fun Memo.getThemePic() =
            mAttachments.
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
        toolbar.background = resources.getDrawable(R.drawable.gradient_black_transparent)
        layout_header.background = resources.getDrawable(R.drawable.reverse_gradient_black_tranparent)
        layout_appbar.addOnOffsetChangedListener { appbar, verticalOffset ->
            // verticalOffset is always minus
            val multiplier = (-verticalOffset / (appbar.totalScrollRange * 0.75f)).coerceAtMost(1.0f)
            val alpha = (255 * (1 - multiplier)).toInt()
            toolbar.background.alpha = alpha
            layout_header.background.alpha = alpha
        }
        layout_appbar.addOnOffsetChangedListener(TitleTranslateManager(edit_title, 20.0.toPx()))
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

    private var mAttachmentAdapter: AttachmentsAdapter? = null
}

class TitleTranslateManager(private val view: View,
                            private val totalDistance: Int) : AppBarLayout.OnOffsetChangedListener {
    private val right by lazy { view.right }
    private val left by lazy { view.left }
    private val top by lazy { view.top }
    private val bottom by lazy { view.bottom }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        val offset = totalDistance * -verticalOffset / appBarLayout.totalScrollRange
        view.layout(left + offset, top, right - offset, bottom)
        if (view is TextView) view.text = view.text
    }
}