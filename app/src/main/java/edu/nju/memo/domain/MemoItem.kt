package edu.nju.memo.domain

import edu.nju.memo.common.toNotBlank

/**
 * The core entity represents a memo item.
 *
 * This item itself only carry **one piece of plain text content**. Extra content or content with
 * mime type of non-text/plain are all described by [Attachment].
 *
 * Detailed topic about attachment:
 * * If a certain attachment has no [uri][Attachment.uri] && its [content][Attachment.content]
 * equals to [item's content][MemoItem.content], it will be dropped. This is called `trim`.
 * * If the first attachment's [type][Attachment.type] is `text/plain`(means it has no [uri][Attachment.uri]) &&
 * [item's content][MemoItem.content] is absent, the attachment is dropped, meanwhile [item's content][MemoItem.content]
 * is set as [Attachment.content].
 * This is called `absorb`.
 *
 * See package-info.java for more details.
 *
 * @author [Cleveland Alto](mailto:tinker19981@hotmail.com)
 * */
class MemoItem() {
    /**
     * Row id in db.
     * */
    var id = 0L
    /**
     * Title. Not null, empty when absent.
     * */
    var title = ""
    /**
     * Content. Not null, empty when absent.
     *
     * This must be plain text, if only the third part app stand by the design guide.
     * */
    var content = ""
    /**
     * Created time. Actually, instantiated time.
     * */
    var createTime = System.currentTimeMillis()
    /**
     * Whether being read.
     * Don't forget set this.
     * */
    var isRead = false
    /**
     * Attachments. Refer to [Attachment] for detail.
     * */
    var attachments = mutableListOf<Attachment>()
    /**
     * Tags.
     * */
    var tags = mutableListOf<String>()

    constructor(title: String?, content: String?) : this() {
        this.title = title.toNotBlank()
        this.content = content.toNotBlank()
    }

    fun addAttachment(attachment: Attachment) = attachments.add(attachment)

    fun removeAttachment(attachment: Attachment) = attachments.remove(attachment)

    fun addTag(tag: String) = tags.add(tag)

    fun removeTag(tag: String) = tags.remove(tag)

    override fun toString() =
            "MemoItem(id=$id, title='$title', createTime=$createTime, isRead=$isRead, tags=$tags\n content='$content'\n attachments=$attachments)"

    fun trimmedAttachments() = attachments.apply { removeAll(Attachment::isEmpty) }
}