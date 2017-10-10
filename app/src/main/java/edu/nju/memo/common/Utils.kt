package edu.nju.memo.common

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.annotation.ColorInt
import android.text.method.KeyListener
import android.text.method.TextKeyListener
import android.text.util.Linkify
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.EditText
import edu.nju.memo.MainApplication
import java.io.File
import java.net.URI
import kotlin.coroutines.experimental.buildSequence

/**
 * Created by tinker on 2017/9/12.
 *
 */

val backgroundTag = "background"
val keyListenerTag = "keyListener"
val positionTag = "position"

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

fun Uri?.toFile() = this?.takeIf { this.toString() != "null" }?.let { File(URI(this.toString())) }

fun Uri?.toFilePath() = toFile()?.path

fun dimensionPixelSize(id: Int) = application.resources.getDimensionPixelSize(id)

inline fun <reified T> View.child(id: Int) = this.findViewById(id) as T

fun color(id: Int) = application.resources.getColor(id)

fun string(id: Int) = application.resources.getString(id)

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

@SuppressWarnings("UNCHECKED_CAST")
fun View.setTag(key: String, value: Any?): View {
    if (tag !is MutableMap<*, *>?) return this
    if (tag == null) tag = mutableMapOf<String, Any?>()

    (tag as MutableMap<String, Any?>)[key] = value
    return this
}

@SuppressWarnings("UNCHECKED_CAST")
inline fun <reified T> View.getTag(key: String): T? {
    if (tag !is MutableMap<*, *>) return null

    return (tag as MutableMap<String, Any?>)[key] as T?
}

fun EditText.readOnly() = apply {
    setTag(backgroundTag, background)
    setTag(keyListenerTag, keyListener)

    setOnLongClickListener { _ -> true }
    background = null
    keyListener = null
    linkClickable(true)
}

fun EditText.writable() = apply {
    background = getTag(backgroundTag) ?: background
    keyListener = getTag(keyListenerTag) ?: keyListener

    setOnLongClickListener { _ -> false }
    linkClickable(false)
}

fun EditText.linkClickable(whether: Boolean) {
    linksClickable = whether
    autoLinkMask = Linkify.ALL.takeIf { whether } ?: 0
    setText(text.toString())
}

fun EditText.editable(flag: Boolean) = apply { if (flag) writable() else readOnly() }

@ColorInt
fun alphaBlend(@ColorInt front: Int, @ColorInt overlaid: Int): Int {
    val alpha = alpha(front) / 255.0
    return rgb((red(front) * alpha + (1 - alpha) * red(overlaid)).toInt(),
            (green(front) * alpha + (1 - alpha) * green(overlaid)).toInt(),
            (blue(front) * alpha + (1 - alpha) * blue(overlaid)).toInt())
}

val mimeMap = MimeTypeMap.getSingleton()

val application = MainApplication.APP

val windowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager

val displayMetric = DisplayMetrics().apply { windowManager.defaultDisplay.getMetrics(this) }

val density = displayMetric.density

fun Double.toPx() = (this * density + 0.5f).toInt()

fun View.visible(visibility: Int): View {
    if (this.visibility != visibility) this.visibility = visibility
    return this
}
