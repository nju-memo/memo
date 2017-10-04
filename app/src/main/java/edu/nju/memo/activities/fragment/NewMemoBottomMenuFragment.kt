package edu.nju.memo.activities.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import edu.nju.memo.R

/**
 * @author [Cleveland Alto](mailto:tinker19981@hotmail.com)
 */
class NewMemoBottomMenuFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_new_memo_bottom_memu, container, false)
}