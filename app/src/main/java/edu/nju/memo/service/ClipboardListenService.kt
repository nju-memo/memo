package edu.nju.memo.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import edu.nju.memo.MainActivity
import edu.nju.memo.R
import edu.nju.memo.common.asArr
import edu.nju.memo.manager.ViewManager
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast

/**
 * Created by tinker on 2017/9/12.
 *
 * Service listening on clipboard changes
 */
class ClipboardListenService : Service(), ClipboardManager.OnPrimaryClipChangedListener {

    private val clipboardManager by lazy { getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }
    private var prevTime = 0L
    private val THRESHOLD = 500

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY.also {
        clipboardManager.addPrimaryClipChangedListener(this)
        startForeground(1, Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("稍后再看")
                .setContentText("正在监听剪贴板")
                .build())
    }

    override fun onPrimaryClipChanged() = System.currentTimeMillis().let { curTime ->
        if (prevTime == 0L || curTime - prevTime > THRESHOLD) {
            toast("Copied!!")
        }
        prevTime = curTime
    }

    override fun onDestroy() {
        stopForeground(true)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}