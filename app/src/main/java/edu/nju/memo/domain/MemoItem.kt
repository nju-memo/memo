package edu.nju.memo.domain

/**
 * Created by tinker on 2017/9/19.
 */
class MemoItem() {
    var id = 0L
    var title = ""
    var content = ""
    var createTime = 0L
    var isRead = false
    private var _attachments: MutableList<Attachment>? = null
    var attachments: MutableList<Attachment>
        get() {
            if (_attachments == null) _attachments = mutableListOf()
            return _attachments!!
        }
        set(value) = value.let { attachments = it }
    var tags = mutableListOf("默认")

    constructor(title: String?, content: String?) : this() {
        this.title = title ?: ""
        this.content = content ?: ""
    }

    fun addAttachment(attachment: Attachment) = attachments.add(attachment)

    fun removeAttachment(attachment: Attachment) = attachments.remove(attachment)

    override fun toString() =
            "MemoItem(title='$title', content='$content', createTime=$createTime, isRead=$isRead"
}