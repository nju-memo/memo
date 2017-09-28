package edu.nju.memo.domain

import android.webkit.MimeTypeMap
import edu.nju.memo.common.mimeMap
import edu.nju.memo.common.toNotBlank

/**
 * Created by tinker on 2017/9/19.
 */
private val defaultList = listOf("默认")

class MemoItem() {
    var id = 0L
    var title = ""
    var type = "text/plain"
    var content = ""
    var createTime = System.currentTimeMillis()
    var isRead = false
    var attachments = mutableListOf<Attachment>()
    var _tags = mutableListOf<String>()
    var tags
        get() = if (_tags.isEmpty()) defaultList else _tags.toList()
        set(value) = value.let { _tags = value.toMutableList() }

    constructor(title: String?, content: String?, type: String) : this() {
        this.title = title.toNotBlank()
        this.content = content.toNotBlank()
        this.type = type
    }

    fun addAttachment(attachment: Attachment) = attachments.add(attachment)

    fun removeAttachment(attachment: Attachment) = attachments.remove(attachment)

    fun addTag(tag: String) {
        if (_tags === defaultList) _tags = mutableListOf()
        _tags.add(tag)
    }

    fun removeTag(tag: String) = _tags.remove(tag)

    override fun toString() =
            "MemoItem(id=$id, title='$title',  type=$type, createTime=$createTime, isRead=$isRead, tags=$tags\n content='$content'\n attachments=$attachments)"

    fun trimmedAttachments() = attachments.apply { removeAll(Attachment::isEmpty) }
}