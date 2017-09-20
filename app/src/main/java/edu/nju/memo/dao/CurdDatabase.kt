package edu.nju.memo.dao

/**
 * Created by tinker on 2017/9/20.
 */
interface CurdDatabase {
    fun add(entity: Any): Long
}