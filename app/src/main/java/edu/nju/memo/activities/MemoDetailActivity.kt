package edu.nju.memo.activities

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.support.v7.widget.LinearLayoutManager
import android.view.Window
import android.widget.EditText
import edu.nju.memo.R
import edu.nju.memo.common.*
import edu.nju.memo.domain.Memo
import kotlinx.android.synthetic.main.fragment_memo_detail.*
import kotlinx.android.synthetic.main.view_image_stubbed.*
import org.jetbrains.anko.imageBitmap
import java.io.InputStream

/**
 * @author [Cleveland Alto](mailto:tinker19981@hotmail.com)
 */
class MemoDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_memo_detail)

        setSupportActionBar(toolbar)

        intent.safeExtra<Memo>("memo")?.let { renderContent(it) }
    }

    private fun renderContent(memo: Memo) {
        layout_collapsing.setTitle(memo.title)
        setHeaderPicture(memo.getThemePic())

        with(memo) {
            recycler_attachments.layoutManager = LinearLayoutManager(application)
            recycler_attachments.adapter = AttachmentsAdapter(attachments)
        }
    }

    private fun EditText.readOnly() = apply {
        background = null
        keyListener = null
        setTextIsSelectable(true)
        setTextColor(color(R.color.white))
        setLinkTextColor(color(R.color.white))
    }

    private fun Memo.getThemePic() = attachments.find { it.uriType.startsWith("imageView/*") }?.uri

    private fun setHeaderPicture(picUri: Uri?) {
        picUri?.toFile()?.takeIf { it.exists() }?.
                let { decodeBitmap(it.path, height = dimensionPixelSize(R.dimen.appbar_height)) }?.
                also { view_image_header.imageBitmap = it }?.
                let { Palette.from(it) }?.
                generate { palette ->
                    palette.
                            let {
                                it.getDarkVibrantColor(color(R.color.colorPrimary)) to
                                        it.getLightMutedColor(color(R.color.white))
                            }.
                            let { (bgColor, titleColor) ->
                                layout_collapsing.setStatusBarScrimColor(bgColor)
                                layout_collapsing.setContentScrimColor(bgColor)
                                layout_collapsing.setBackgroundColor(bgColor)
                                layout_collapsing.setCollapsedTitleTextColor(titleColor)
                                layout_collapsing.setExpandedTitleColor(bgColor)
                            }
                }

    }
}