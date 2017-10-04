package edu.nju.memo.domain

import android.os.Parcel
import android.os.Parcelable
import edu.nju.memo.common.toNotBlank
import java.util.*

/**
 * The core entity represents a memo item.
 *
 * This item itself only carry **one piece of plain textView summary**. Extra summary or summary with
 * mime uriType of non-textView/plain are all described by [Attachment].
 *
 * Detailed topic about attachment:
 * * If a certain attachment has no [uri][Attachment.uri] && its [summary][Attachment.text]
 * equals to [item's summary][Memo.summary], it will be dropped. This is called `trim`.
 * * If the first attachment's [uriType][Attachment.uriType] is `textView/plain`(means it has no [uri][Attachment.uri]) &&
 * [item's summary][Memo.summary] is absent, the attachment is dropped, meanwhile [item's summary][Memo.summary]
 * is set as [Attachment.text].
 * This is called `absorb`.
 *
 * See package-info.java for more details.
 *
 * @author [Cleveland Alto](mailto:tinker19981@hotmail.com)
 * */
class Memo() : Parcelable {
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
     * This must be plain textView, if only the third part app stand by the design guide.
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

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        title = parcel.readString()
        summary = parcel.readString()
        createTime = parcel.readLong()
        isRead = parcel.readByte() != 0.toByte()
        parcel.readStringList(tags)
        @Suppress("UNCHECKED_CAST")
        parcel.readParcelableArray(Attachment::class.java.classLoader)?.let {
            attachments = Arrays.copyOf(it, it.size, Array<Attachment>::class.java).toMutableList()
        }
    }

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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(title)
        parcel.writeString(summary)
        parcel.writeLong(createTime)
        parcel.writeByte(if (isRead) 1 else 0)
        parcel.writeStringList(tags)
        parcel.writeParcelableArray(attachments.toTypedArray(), flags)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Memo> {
        override fun createFromParcel(parcel: Parcel): Memo = Memo(parcel)

        override fun newArray(size: Int): Array<Memo?> = arrayOfNulls(size)
    }
}