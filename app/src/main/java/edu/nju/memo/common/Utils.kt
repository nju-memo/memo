package edu.nju.memo.common

import android.content.Intent
import android.util.Log
import android.webkit.MimeTypeMap
import edu.nju.memo.MainApplication
import kotlin.coroutines.experimental.buildSequence

/**
 * Created by tinker on 2017/9/12.
 *
 */

inline fun <reified T> T.info(log: Any?) = Log.i(T::class.java.canonicalName, "$log")

inline fun <reified T> T.warning(log: Any?) = Log.w(T::class.java.canonicalName, "$log")

inline fun <reified T> T.asArr() = arrayOf(this)

fun <T, R> T.iter(count: Int, getter: T.(Int) -> R) = buildSequence { for (i in 0 until count) yield(getter(i)) }

fun <T, R> T.iter(getCount: T.() -> Int, getter: T.(Int) -> R) = this.iter(getCount(), getter)

inline fun <reified T> Intent.extra(key: String) = extras.get(key) as T

fun Any?.safeToString() = this?.toString() ?: ""

fun String?.toNotBlank() = this.takeIf { !it.isNullOrBlank() } ?: ""

fun Boolean.ifTrue(func: () -> Unit) = also { if (it) func() }

fun <T> Iterable<T>.peek(func: (T) -> Unit) = apply { forEach { func(it) } }

val mimeMap = MimeTypeMap.getSingleton()

val application = MainApplication.APP

