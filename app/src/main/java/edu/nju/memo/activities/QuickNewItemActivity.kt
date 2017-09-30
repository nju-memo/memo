package edu.nju.memo.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import com.android.internal.util.Predicate
import edu.nju.memo.MainApplication
import edu.nju.memo.R
import edu.nju.memo.common.info
import edu.nju.memo.common.safeToString
import edu.nju.memo.core.parser.MemoItemFactoryImpl
import edu.nju.memo.dao.CachedMemoDao
import kotlinx.android.synthetic.main.activity_image_temp.*
import kotlinx.android.synthetic.main.activity_quick_new_item.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast
import java.io.File
import java.net.URI

/**
 * For development test.
 * */
class QuickNewItemActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_new_item)
        val item = MemoItemFactoryImpl.getMemoItem(intent)
        CachedMemoDao.insert(item)

        info(item)
        indeterminateProgressDialog("$item")
    }
}
