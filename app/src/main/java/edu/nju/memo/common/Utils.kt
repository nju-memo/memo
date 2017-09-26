package edu.nju.memo.common

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.util.Log
import edu.nju.memo.MainApplication
import kotlin.coroutines.experimental.buildSequence

/**
 * Created by tinker on 2017/9/12.
 *
 */

inline fun <reified T> T.info(log: Any?) = Log.i(T::class.java.canonicalName, "$log")

inline fun <reified T> T.warning(log: Any?) = Log.w(T::class.java.canonicalName, "$log")

inline fun <reified T> T.asArr() = arrayOf(this)

inline fun <reified T> T.asList() = listOf(this)

fun <T, R> T.iter(count: Int, getter: T.(Int) -> R) = buildSequence { for (i in 0 until count) yield(getter(i)) }

fun <T, R> T.iter(getCount: T.() -> Int, getter: T.(Int) -> R) = this.iter(getCount(), getter)

fun <T> choose(a: T, b: T, judge: T.() -> Boolean) = if (a.judge()) a else b

inline fun <reified T> Intent.extra(key: String) = extras.get(key) as T

fun Any?.safeToString() = this?.toString() ?: ""
