package edu.nju.memo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import edu.nju.memo.service.ClipboardListenService

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startService(Intent(applicationContext, ClipboardListenService::class.java))
    }
}
