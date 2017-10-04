package edu.nju.memo.common

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.MimeTypeMap
import edu.nju.memo.MainApplication
import java.io.File
import java.io.InputStream
import java.net.URI
import kotlin.coroutines.experimental.buildSequence

/**
 * Created by tinker on 2017/9/12.
 *
 */

inline fun <reified T> T.info(log: Any?) = Log.i(T::class.java.canonicalName, "$log")

inline fun <reified T> T.warning(log: Any?) = Log.w(T::class.java.canonicalName, "$log")

inline fun <reified T> T.asArr() = arrayOf(this)

fun <T, R> T.sequence(count: Int, getter: T.(Int) -> R) = buildSequence { for (i in 0 until count) yield(getter(i)) }

fun <T, R> T.sequence(getCount: T.() -> Int, getter: T.(Int) -> R) = this.sequence(getCount(), getter)

inline fun <reified T> Intent.extra(key: String) = extras.get(key) as T

inline fun <reified T> Intent.safeExtra(key: String) = extras?.get(key) as T?

fun Any?.safeToString() = this?.toString() ?: ""

fun String?.toNotBlank() = this.takeIf { !it.isNullOrBlank() } ?: ""

fun Boolean.ifTrue(func: () -> Unit) = also { if (it) func() }

fun <T> Iterable<T>.peek(func: (T) -> Unit) = apply { forEach { func(it) } }

fun <T, R> T.letIf(predication: Boolean, func: (T) -> R) = if (predication) func(this) else null

fun Uri?.toFile() = File(URI(this.safeToString()))

fun Uri?.toFilePath() = toFile().path

fun dimensionPixelSize(id: Int) = application.resources.getDimensionPixelSize(id)

inline fun <reified T> View.child(id: Int) = this.findViewById(id) as T

fun color(id: Int) = application.resources.getColor(id)

fun decodeBitmap(file: String, width: Int = displayMetric.widthPixels, height: Int = displayMetric.heightPixels) =
        with(BitmapFactory.Options()) {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(file, this)
            inSampleSize = // pretty nice
                    (outWidth / width to outHeight / height).let { (scaleX, scaleY) ->
                        when {
                            (scaleY in 1..(scaleX - 1)) -> scaleX
                            (scaleX in 1..(scaleY - 1)) -> scaleY
                            else -> 1
                        }
                    }
            inJustDecodeBounds = false
            BitmapFactory.decodeFile(file, this)
        }

val mimeMap = MimeTypeMap.getSingleton()

val application = MainApplication.APP

val windowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager

val displayMetric = DisplayMetrics().apply { windowManager.defaultDisplay.getMetrics(this) }
