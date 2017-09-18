package edu.nju.memo.activities

import android.app.Activity
import android.content.Intent
import android.support.v7.app.ActionBarActivity
import android.os.Bundle
import edu.nju.memo.R
import edu.nju.memo.common.info

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
        if (intent != null) {
            info(intent.scheme)
            info(intent.type)
            info(intent.clipData.description)
            with(intent.clipData) {
                for (i in 0 until itemCount) {
                    info(getItemAt(i))
                    info(getItemAt(i).text)
                    info(getItemAt(i).uri)
                }

            }
            info(intent.dataString)
            with(intent.extras) {
                info(getString(Intent.EXTRA_TEXT))
                info(getString(Intent.EXTRA_HTML_TEXT))
                info(getString(Intent.EXTRA_TITLE))
            }
        }
    }
}
