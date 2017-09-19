package edu.nju.memo.domain

/**
 * Created by tinker on 2017/9/19.
 */
class MemoItem() {

    var title = ""
    var content = ""
    var createTime = 0L
    var hasRead = false
    lateinit var attachment: List<Attachment>

    constructor(title: String?, content: String?) : this() {
        this.title = title ?: ""
        this.content = content ?: ""
    }

    override fun toString() =
            "MemoItem(title='$title', content='$content', createTime=$createTime, hasRead=$hasRead"
}