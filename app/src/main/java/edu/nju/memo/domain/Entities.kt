package edu.nju.memo.domain

import org.jetbrains.anko.db.*

/**
 * Created by tinker on 2017/9/20.
 */

internal val ENTITIES = arrayOf("MEMO_T", "ATTACHMENT_T", "TAG_T")
internal val CLASSES = mapOf(
        Attachment::class to "ATTACHMENT_T",
        Memo::class to "MEMO_T",
        String::class to "TAG_T"
)

internal val TABLES = mapOf(
        Memo::class to arrayOf(
                "TITLE" to TEXT,
                "CONTENT" to TEXT,
                "CREATE_TIME" to INTEGER,
                "READ" to INTEGER),
        Attachment::class to arrayOf(
                "IID" to INTEGER,
                "URI" to TEXT,
                "TYPE" to TEXT,
                "CONTENT" to TEXT),
        String::class to arrayOf(
                "IID" to INTEGER,
                "TAG" to TEXT)
)

inline internal fun <reified T> tableOf() = CLASSES[T::class]!!

inline internal fun <reified T> fieldsOf() = TABLES[T::class]!!

internal fun Memo.toNamedArray() = arrayOf(
        "TITLE" to mTitle,
        "CONTENT" to mSummary,
        "CREATE_TIME" to createTime,
        "READ" to isRead
)

internal fun Attachment.toNamedArray() = arrayOf(
        "URI" to uri.toString(),
        "TYPE" to uriType,
        "CONTENT" to text
)
