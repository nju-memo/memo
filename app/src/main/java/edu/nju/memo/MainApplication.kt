package edu.nju.memo

import android.app.Application
import edu.nju.memo.dao.initDatabase

/**
 * Created by tinker on 2017/9/20.
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initDatabase()
    }
}