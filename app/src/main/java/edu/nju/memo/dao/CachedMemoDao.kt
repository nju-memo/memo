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
        memoItems[item.id] = item.copy()
    }

    private fun deleteCache(id: Long) {
        select(id)?.let { AttachmentFileCache.deleteCache(it.attachments) }
        memoItems.remove(id)
    }

    private fun SQLiteDatabase.insertMemoItem(item: MemoItem) =
            insert(tableOf<MemoItem>(), *item.toNamedArray()).takeIf { it != -1L }
                    ?: throw SQLiteException("Failed to insert")

    private fun SQLiteDatabase.insertAttachments(attachments: List<Attachment>, itemId: Long) =
            attachments.map { attachment ->
                attachment.apply {
                    id = insert(tableOf<Attachment>(), "IID" to itemId, *attachment.toNamedArray()).
                            takeIf { -1L != it } ?: throw SQLiteException("Failed to insert")
                }
            }.let { AttachmentFileCache.cacheToFile(it) }

    private fun SQLiteDatabase.insertTags(tags: List<String>, itemId: Long) =
            tags.filter { -1L == insert(tableOf<String>(), "TAG" to it, "IID" to itemId) }.
                    forEach { throw SQLiteException("Failed to insert") }

    private fun SQLiteDatabase.deleteAttachments(attachments: List<Attachment>, itemId: Long) =
            attachments.peek {
                delete(tableOf<Attachment>(), "ROWID=${it.id} AND IID=$itemId")
            }.let { AttachmentFileCache.deleteCache(it) }

    private fun SQLiteDatabase.deleteTags(tags: List<String>, itemId: Long) =
            tags.forEach { delete(tableOf<String>(), "TAG=$it AND IID=$itemId") }

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

    override fun insert(item: MemoItem) = AttachmentFileCache.cacheToFile(item.attachments) &&
            db.use {
                withTx {
                    item.id = insertMemoItem(item).
                            also { id -> insertTags(item.tags, id); insertAttachments(item.attachments, id) }
                }
            }.ifTrue { insertCache(item) } // add to cache after all

    override fun delete(id: Long) = db.use {
        withTx {
            delete(tableOf<MemoItem>(), "ROWID = $id")
            delete(tableOf<Attachment>(), "IID = $id")
            delete(tableOf<String>(), "IID = $id")
        }
    }.ifTrue { deleteCache(id) } // delete cache after all

    override fun delete(test: Predicate<MemoItem>) = memoItems.filterValues { test.apply(it) }.
            filterKeys { delete(it) }.map { (_, v) -> v }

    override fun update(item: MemoItem) = db.use {
        select(item.id)?.let { old ->
            crossMinus(old.attachments, item.attachments).let { (deletedAttachment, addedAttachment) ->
                // create cache at first
                AttachmentFileCache.cacheToFile(addedAttachment)
                        && withTx {
                    insertAttachments(addedAttachment, item.id)
                    deleteAttachments(deletedAttachment, item.id)
                    crossMinus(old.tags, item.tags).let { (deletedTags, addedTags) ->
                        insertTags(addedTags, item.id)
                        deleteTags(deletedTags, item.id)
                    }
                    update(tableOf<MemoItem>(), *item.toNamedArray()).whereSimple("ROWID = ${item.id}")
                } // and delete cache at last to make risk least
                        // result of deleting cache doesn't matter. If we reach here, return true anyway
                        && (AttachmentFileCache.deleteCache(deletedAttachment) || true)
            }.ifTrue { insertCache(item) } // update the cache after all
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