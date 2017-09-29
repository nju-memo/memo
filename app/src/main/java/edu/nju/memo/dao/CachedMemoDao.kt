package edu.nju.memo.dao

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.net.Uri
import com.android.internal.util.Predicate
import edu.nju.memo.MainApplication
import edu.nju.memo.common.Function
import edu.nju.memo.common.ifTrue
import edu.nju.memo.common.peek
import edu.nju.memo.common.warning
import edu.nju.memo.domain.*
import org.jetbrains.anko.db.*
import java.io.IOException

object CachedMemoDao : MemoDao {
    private val db by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        object : ManagedSQLiteOpenHelper(MainApplication.APP, "memo.db", null, 1) {
            override fun onCreate(db: SQLiteDatabase) {
                TABLES.forEach { (type, fields) -> db.createTable(CLASSES[type]!!, true, *fields) }
            }

            override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
                ENTITIES.forEach { db.dropTable(it, true) }
            }
        }
    }
    private val memoItems by lazy { refresh() }

    private fun insertCache(item: MemoItem) {
        memoItems[item.id] = item
    }

    private fun deleteCache(id: Long) = memoItems.remove(id)

    private fun insertMemoItem(item: MemoItem) = db.use {
        insert(tableOf<MemoItem>(), *item.toNamedArray()).
                takeIf { it != -1L } ?: throw SQLiteException("Failed to insert")
    }

    private fun insertAttachments(attachments: List<Attachment>, itemId: Long) = db.use {
        attachments.peek { attachment ->
            insert(tableOf<Attachment>(), *attachment.toNamedArray(), "IID" to itemId).
                    takeIf { it != -1L }?.
                    let { attachment.id = it } ?: throw SQLiteException("Failed to insert")
        }.let { AttachmentFileCache.cacheToFile(attachments) }
    }

    private fun insertTags(tags: List<String>, itemId: Long) = db.use {
        tags.filter { -1L == insert(tableOf<String>(), "TAG" to it, "IID" to itemId) }.
                forEach { throw SQLiteException("Failed to insert") }
    }

    private fun deleteAttachments(attachments: List<Attachment>, itemId: Long) = db.use {
        attachments.peek {
            delete(tableOf<Attachment>(), "ROWID=${it.id} AND IID=$itemId")
        }.let { AttachmentFileCache.deleteCache(it) }
    }

    private fun deleteTags(tags: List<String>, itemId: Long) = db.use {
        tags.forEach { delete(tableOf<String>(), "TAG=$it AND IID=$itemId") }
    }

    private fun <T> crossMinus(c0: Collection<T>, c1: Collection<T>) = (c0 - c1) to (c1 - c0)

    private fun SQLiteDatabase.withTx(code: SQLiteDatabase.() -> Unit): Boolean {
        var res = false
        try {
            beginTransaction()
            code()
            setTransactionSuccessful()
            res = true
        } catch (e: TransactionAbortException) {
            warning(e)
        } finally {
            endTransaction()
            return res
        }
    }

    override fun insert(item: MemoItem) = db.use {
        withTx {
            insertMemoItem(item).let { id ->
                insertTags(item.tags, id)
                insertAttachments(item.attachments, id)
                item.id = id
            }
        }.ifTrue { insertCache(item) }
    }

    override fun delete(id: Long) = db.use {
        withTx {
            delete(tableOf<MemoItem>(), "ROWID = $id")
            delete(tableOf<Attachment>(), "IID = $id")
            delete(tableOf<String>(), "IID = $id")
            select(id)?.let { AttachmentFileCache.deleteCache(it.attachments) }
        }.ifTrue {
            deleteCache(id)
        }
    }

    override fun delete(test: Predicate<MemoItem>) = memoItems.filterValues { test.apply(it) }.
            filterKeys { delete(it) }.map { (_, v) -> v }

    override fun update(item: MemoItem) = db.use {
        select(item.id)?.let { old ->
            withTx {
                update(tableOf<MemoItem>(), *item.toNamedArray()).whereSimple("ROWID = ${item.id}")
                crossMinus(old.tags, item.tags).let { (deleted, added) ->
                    deleteTags(deleted, item.id)
                    insertTags(added, item.id)
                }
                crossMinus(old.attachments, item.attachments).let { (deleted, added) ->
                    insertAttachments(added, item.id)
                    deleteAttachments(deleted, item.id)

                    AttachmentFileCache.cacheToFile(added)
                    AttachmentFileCache.deleteCache(deleted)
                }
            }.ifTrue { insertCache(item) }
        } ?: false
    }

    override fun update(test: Predicate<MemoItem>, func: Function<MemoItem, MemoItem>) =
            select(test).map { func.apply(it) }.filter { update(it) }

    override fun updateOrInsert(item: MemoItem) = db.use {
        (!update(item)) && insert(item) // short circuit, when update fails try insert
    }

    override fun select(id: Long) = memoItems[id]

    override fun select(test: Predicate<MemoItem>) = memoItems.filterValues { test.apply(it) }.map { (_, v) -> v }

    override fun selectAll() = memoItems.values.toList()

    override fun refresh() = synchronized(this) {
        db.use {
            select(tableOf<MemoItem>(), "ROWID", "*").parseList(
                    rowParser { id: Long, title: String, content: String, createTime: Long, read: Int ->
                        MemoItem(title, content).apply {
                            this.id = id
                            this.createTime = createTime
                            this.isRead = read != 0
                        }
                    }
            ).map {
                it.tags = select(tableOf<String>(), "TAG").
                        parseList(rowParser { tag: String -> tag }).toMutableList();it
            }.map {
                it.attachments = select(tableOf<Attachment>(), "ROWID", "*").
                        parseList(rowParser { id: Long, _: Long, uri: String, type: String, content: String ->
                            Attachment(Uri.parse(uri), content, type).apply { this.id = id;this.cacheState = CACHED }
                        }).toMutableList();it
            }.map { it.id to it }.let { mutableMapOf(*it.toTypedArray()) }
        }
    }
}
