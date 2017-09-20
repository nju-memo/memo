package edu.nju.memo.domain

import org.jetbrains.anko.db.*
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

/**
 * Created by tinker on 2017/9/20.
 */

val ENTITIES = arrayOf("MEMOITEM", "ATTACHMENT")
val CLASSES = mapOf(
        Attachment::class.java to "ATTACHMENT",
        MemoItem::class.java to "MEMOITEM"
)

val TABLES = mapOf(
        Attachment::class.java to arrayOf(
                "AID" to (INTEGER + PRIMARY_KEY + AUTOINCREMENT),
                "URI" to TEXT,
                "CONTENT" to TEXT,
                "IID" to INTEGER,
                FOREIGN_KEY("IID", "MEMOITEM", "IID")),
        MemoItem::class.java to arrayOf(
                "IID" to (INTEGER + PRIMARY_KEY + AUTOINCREMENT),
                "TITLE" to TEXT,
                "CONTENT" to TEXT,
                "CREATE_TIME" to INTEGER,
                "READ" to INTEGER)
)

val FIELDS = mapOf(
        Attachment::class.java to mapOf(
                "AID" to Attachment::id.getter,
                "URI" to Attachment::uri.getter,
                "CONTENT" to Attachment::content.getter,
                "IID" to Attachment::itemId.getter
        ),
        MemoItem::class.java to mapOf(
                "IID" to MemoItem::id.getter,
                "TITLE" to MemoItem::title.getter,
                "CONTEXT" to MemoItem::content.getter,
                "CREATE_TIME" to MemoItem::createTime.getter,
                "READ" to MemoItem::hasRead.getter
        )
)
