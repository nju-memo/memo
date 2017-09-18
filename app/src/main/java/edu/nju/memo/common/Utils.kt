package edu.nju.memo.common

import android.util.Log

/**
 * Created by tinker on 2017/9/12.
 *
 */

inline fun <reified T> T.info(log: Any?) = Log.i(T::class.java.canonicalName, "$log")

inline fun <reified T> T.asArr() = arrayOf(this)