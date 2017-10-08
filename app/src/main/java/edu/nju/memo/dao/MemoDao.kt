package edu.nju.memo.dao

import com.android.internal.util.Predicate
import edu.nju.memo.common.Function
import edu.nju.memo.domain.Memo

/**
 * Dao to manipulate Memo, tag and attachment cascades
 *
 * Created by tinker on 2017/9/22.
 */
interface MemoDao {
    /**
     * insert a new item.
     *
     * Notice: the instance id field is ignored when insert.
     *
     * @param item new item
     * @return operation's result, the item's id field will be set as well
     * */
    fun insert(item: Memo): Boolean

    /**
     * Delete by id.
     *
     * @param id item's id
     * @return operation's result
     * */
    fun delete(id: Long): Boolean

    /**
     * Delete items by conditions indicated by `test`
     *
     * @param test condition to test items, deleted if result is true
     * @return items has been removed
     * */
    fun delete(test: Predicate<Memo>): List<Memo>

    /**
     * Update an item.
     *
     * The item is specified by `item.id`, other field contains values to be updated
     *
     * @param item
     * @return operation's result. false if given id doesn't exist, or the update itself fails
     * */
    fun update(item: Memo): Boolean

    /**
     * Update item specified by `test` result, set their values through `func`
     *
     * @param test criteria to test item
     * @param func setter of new value, take the old item as parameter and return a new item.
     *
     * @return items has been updated successfully
     * @see Function
     * */
    fun update(test: Predicate<Memo>, func: Function<Memo, Memo>): List<Memo>

    /**
     * Update an item if exists, insert it otherwise.
     *
     * @param item
     * @return operation's result
     * */
    fun updateOrInsert(item: Memo): Boolean

    /**
     * Select an item by id.
     *
     * @param id
     * @return item
     * */
    fun select(id: Long): Memo?

    /**
     * Select items by criteria specified by `test`
     *
     * @param test criteria to test items
     * @return selected items
     * */
    fun select(test: Predicate<Memo>): List<Memo>

    /**
     * Get all items
     *
     * @return all items
     * */
    fun selectAll(): List<Memo>

    /**
     * Get all tags.
     *
     * @return all tags
     * */
    fun selectAllTags(): List<String>

    /**
     * Refresh the cacheToFile
     * */
    fun refresh(): MutableMap<Long, Memo>
}