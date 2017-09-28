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

class QuickNewItemActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_new_item)
        // 测试分享收到的intent的内容
        // 目前的结论:
        // 首先根据mimetype分类
        // 对于text/*
        // 首先遍历intent.clipData, 每一项如果有uri就存， 有text就存
        // 这里没有text的话就拿EXTRA_TEXT[Intent.EXTRA_TEXT]
        val item = MemoItemFactoryImpl.getMemoItem(intent)
        CachedMemoDao.insert(item)

        info(item)
        indeterminateProgressDialog("$item")
    }
}
