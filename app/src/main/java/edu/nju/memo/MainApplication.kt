package edu.nju.memo

import android.app.Application

/**
 * Created by tinker on 2017/9/20.
 */
class MainApplication : Application() {
    companion object {
        @JvmStatic
        lateinit var APP: Application
    }

    override fun onCreate() {
        super.onCreate()
        APP = this
    }
}