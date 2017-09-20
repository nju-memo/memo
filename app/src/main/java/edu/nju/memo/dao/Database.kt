package edu.nju.memo.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import edu.nju.memo.domain.CLASSES
import edu.nju.memo.domain.ENTITIES
import edu.nju.memo.domain.FIELDS
import edu.nju.memo.domain.TABLES
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper
import org.jetbrains.anko.db.createTable
import org.jetbrains.anko.db.dropTable
import org.jetbrains.anko.db.insert

/**
 * Database open helper
 * Created by tinker on 2017/9/20.
 */

class Database private constructor(ctx: Context) : CurdDatabase,
        ManagedSQLiteOpenHelper(ctx, "edu.nju.memo.testdb", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        TABLES.forEach { (clazz, fields) -> db.createTable(CLASSES[clazz]!!, true, *fields) }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        ENTITIES.forEach { db.dropTable(it, true) }
    }

    companion object {
        @JvmStatic
        private var instance: Database? = null

        @JvmStatic
        fun init(ctx: Context) {
            if (instance == null) {
                synchronized(Database::class) {
                    if (instance == null) instance = Database(ctx)
                }
            }
        }

        @JvmStatic
        fun instance(): CurdDatabase {
            if (instance == null) throw NullPointerException("Database connection has not been initialized!")

            return instance!!
        }
    }

    private fun <T> doAdd(entity: Any, type: Class<T>) = use {
        insert(CLASSES[type]!!,
                *FIELDS[type]!!.map { (name, getter) -> name to getter.call(entity) }.toTypedArray()
        )
    }

    override fun add(entity: Any) = entity::class.java.let { type ->
        if (type !in CLASSES.keys) throw UnsupportedOperationException("Unmapped Entity: $type")
        doAdd(entity, type)
    }
}

fun Context.initDatabase() = Database.init(applicationContext)


