package edu.nju.memo.domain

import android.os.Parcel
import android.os.Parcelable
import edu.nju.memo.common.toNotBlank
import java.util.*

/**
 * The core entity represents a memo item.
 *
 * This item itself only carry **one piece of plain textView mSummary**. Extra mSummary or mSummary with
 * mime uriType of non-textView/plain are all described by [Attachment].
 *
 * Detailed topic about attachment:
 * * If a certain attachment has no [uri][Attachment.uri] && its [mSummary][Attachment.text]
 * equals to [item's mSummary][Memo.mSummary], it will be dropped. This is called `trim`.
 * * If the first attachment's [uriType][Attachment.uriType] is `textView/plain`(means it has no [uri][Attachment.uri]) &&
 * [item's mSummary][Memo.mSummary] is absent, the attachment is dropped, meanwhile [item's mSummary][Memo.mSummary]
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
    var mTitle = ""
    /**
     * Content. Not null, empty when absent.
     *
     * This must be plain textView, if only the third part app stand by the design guide.
     * */
    var mSummary = ""
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
    var mAttachments = mutableListOf<Attachment>()
    /**
     * Tags.
     * */
    var tags = mutableListOf<String>()

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        mTitle = parcel.readString()
        mSummary = parcel.readString()
        createTime = parcel.readLong()
        isRead = parcel.readByte() != 0.toByte()
        parcel.readStringList(tags)
        @Suppress("UNCHECKED_CAST")
        parcel.readParcelableArray(Attachment::class.java.classLoader)?.let {
            mAttachments = Arrays.copyOf(it, it.size, Array<Attachment>::class.java).toMutableList()
        }
    }

    constructor(title: String?, content: String?) : this() {
        this.mTitle = title.toNotBlank()
        this.mSummary = content.toNotBlank()
    }

    fun addAttachment(attachment: Attachment) = mAttachments.add(attachment)

    fun removeAttachment(attachment: Attachment) = mAttachments.remove(attachment)

    fun addTag(tag: String) = tags.add(tag)

    fun removeTag(tag: String) = tags.remove(tag)

    override fun toString() =
            "Memo(id=$id, mTitle='$mTitle', createTime=$createTime, isRead=$isRead, tags=$tags\n mSummary='$mSummary'\n mAttachments=$mAttachments)"

    fun trimmedAttachments() = mAttachments.apply { removeAll(Attachment::isEmpty) }

    fun copy(): Memo {
        val item = Memo(mTitle, mSummary)
        item.id = id
        item.createTime = createTime
        item.mAttachments = mAttachments.map { it.copy() }.toMutableList()
        item.tags = tags.toMutableList()
        item.isRead = isRead
        return item
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(mTitle)
        parcel.writeString(mSummary)
        parcel.writeLong(createTime)
        parcel.writeByte(if (isRead) 1 else 0)
        parcel.writeStringList(tags)
        parcel.writeParcelableArray(mAttachments.toTypedArray(), flags)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Memo> {
        override fun createFromParcel(parcel: Parcel): Memo = Memo(parcel)

        override fun newArray(size: Int): Array<Memo?> = arrayOfNulls(size)
    }
}