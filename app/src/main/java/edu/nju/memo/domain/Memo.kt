package edu.nju.memo.domain

import edu.nju.memo.common.toNotBlank

/**
 * The core entity represents a memo item.
 *
 * This item itself only carry **one piece of plain text summary**. Extra summary or summary with
 * mime type of non-text/plain are all described by [Attachment].
 *
 * Detailed topic about attachment:
 * * If a certain attachment has no [uri][Attachment.uri] && its [summary][Attachment.text]
 * equals to [item's summary][Memo.summary], it will be dropped. This is called `trim`.
 * * If the first attachment's [type][Attachment.type] is `text/plain`(means it has no [uri][Attachment.uri]) &&
 * [item's summary][Memo.summary] is absent, the attachment is dropped, meanwhile [item's summary][Memo.summary]
 * is set as [Attachment.text].
 * This is called `absorb`.
 *
 * See package-info.java for more details.
 *
 * @author [Cleveland Alto](mailto:tinker19981@hotmail.com)
 * */
class Memo() {
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
    var summary = ""
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
        this.summary = content.toNotBlank()
    }

    fun addAttachment(attachment: Attachment) = attachments.add(attachment)

    fun removeAttachment(attachment: Attachment) = attachments.remove(attachment)

    fun addTag(tag: String) = tags.add(tag)

    fun removeTag(tag: String) = tags.remove(tag)

    override fun toString() =
            "Memo(id=$id, title='$title', createTime=$createTime, isRead=$isRead, tags=$tags\n summary='$summary'\n attachments=$attachments)"

    fun trimmedAttachments() = attachments.apply { removeAll(Attachment::isEmpty) }

    fun copy(): Memo {
        val item = Memo(title, summary)
        item.id = id
        item.createTime = createTime
        item.attachments = attachments.toMutableList()
        item.tags = tags.toMutableList()
        item.isRead = isRead
        return item
    }
}