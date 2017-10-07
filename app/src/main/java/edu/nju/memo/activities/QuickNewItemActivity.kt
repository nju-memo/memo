package edu.nju.memo.activities

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import edu.nju.memo.R
import edu.nju.memo.common.info
import edu.nju.memo.core.parser.MemoItemFactoryImpl
import edu.nju.memo.dao.CachedMemoDao
import org.jetbrains.anko.indeterminateProgressDialog

/**
 * For development test.
 * */
class QuickNewItemActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_memo_detail)
        val item = MemoItemFactoryImpl.getMemoItem(intent)
        CachedMemoDao.insert(item)

        info(item)
        indeterminateProgressDialog("$item")
    }
}
