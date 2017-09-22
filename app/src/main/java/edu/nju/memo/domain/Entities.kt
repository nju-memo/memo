package edu.nju.memo.domain

import android.content.ContentValues
import org.jetbrains.anko.db.*
import kotlin.reflect.KClass

/**
 * Created by tinker on 2017/9/20.
 */

internal val ENTITIES = arrayOf("MEMOITEM_T", "ATTACHMENT_T", "TAG_T")
internal val CLASSES = mapOf(
        Attachment::class to "ATTACHMENT_T",
        MemoItem::class to "MEMOITEM_T",
        String::class to "TAG_T"
)

internal val TABLES = mapOf(
        MemoItem::class to arrayOf(
                "TITLE" to TEXT,
                "CONTENT" to TEXT,
                "CREATE_TIME" to INTEGER,
                "READ" to INTEGER),
        Attachment::class to arrayOf(
                "IID" to INTEGER,
                "URI" to TEXT,
                "CONTENT" to TEXT),
        String::class to arrayOf(
                "IID" to INTEGER,
                "TAG" to TEXT)
)

inline internal fun <reified T> tableOf() = CLASSES[T::class]!!

inline internal fun <reified T> fieldsOf() = TABLES[T::class]!!

internal fun MemoItem.toNamedArray() = arrayOf(
        "TITLE" to title,
        "CONTENT" to content,
        "CREATE_TIME" to createTime,
        "READ" to isRead
)

internal fun Attachment.toNamedArray() = arrayOf(
        "URI" to uri,
        "CONTENT" to content
)
