package edu.nju.memo.activities

import android.app.Activity
import android.content.Intent
import android.support.v7.app.ActionBarActivity
import android.os.Bundle
import edu.nju.memo.R
import edu.nju.memo.common.info
import edu.nju.memo.core.parser.MimeItemFactory

class QuickNewItemActivity : Activity() {
    private val factory by lazy { MimeItemFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_new_item)
        // 测试分享收到的intent的内容
        // 目前的结论:
        // 首先根据mimetype分类
        // 对于text/*
        // 首先遍历intent.clipData, 每一项如果有uri就存， 有text就存
        // 这里没有text的话就拿EXTRA_TEXT[Intent.EXTRA_TEXT]
        info(factory.parseIntent(intent, applicationContext))
//        info(intent.component)
//        if (intent != null) {
//            info(intent.scheme)
//            info(intent.type)
//            info(intent.clipData.description)
//            with(intent.clipData) {
//                for (i in 0 until itemCount) {
//                    this@QuickNewItemActivity.info(getItemAt(i))
//                    this@QuickNewItemActivity.info(getItemAt(i).text)
//                    this@QuickNewItemActivity.info(getItemAt(i).uri)
//                    this@QuickNewItemActivity.info(getItemAt(i).coerceToText(applicationContext))
//                }
//
//            }
//            info(intent.dataString)
//            with(intent.extras) {
//                this@QuickNewItemActivity.info(getString(Intent.EXTRA_TEXT))
//                this@QuickNewItemActivity.info(getString(Intent.EXTRA_HTML_TEXT))
//                this@QuickNewItemActivity.info(getString(Intent.EXTRA_TITLE))
//            }
    }
}
