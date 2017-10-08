package edu.nju.memo.domain

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import edu.nju.memo.core.MemoItemFactory
import edu.nju.memo.common.toNotBlank
import edu.nju.memo.dao.AttachmentCache

/**
 * Class describes the extra attached mSummary of the Memo.
 */
class Attachment() : Parcelable {
    /**
     * Row id in db.
     * */
    var id = 0L
    /**
     * Uri of non-text mSummary.
     *
     * This uri is initially from outer. Then it will be cached as a temp file through [AttachmentCache].
     * The [uri] is set to the temp file's uri simultaneously.
     * If is determined to be persisted afterward, the temp file will be moved to a permanent dir. So the [uri]
     * is set to this file after all.
     *
     * Usually schema is `file`.
     * */
    var uri: Uri? = null
    /**
     * Text(plain, html...) mSummary. Not null, empty when absent.
     *
     * (just use WebView)
     * */
    var text = ""
    /**
     * Type.
     *
     * If [uri] is not null, then the uriType is [uri]'s uriType. But this doesn't indicate the [text]
     * is absent.
     *
     * If [uri] is absent, this represents [text]'s uriType
     * */
    var uriType = "text/plain"
    /**
     * Indicate should cache mSummary from [uri] or not.
     *
     * If [uri] is modified after created via [MemoItemFactory], or initially created manually, please
     * also set this field properly. In detail, if the uri is temporary or expires later, set it to [NOT_CACHED].
     * */
    var cacheState = NO_NEED_CACHE

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        uri = parcel.readParcelable(Uri::class.java.classLoader)
        text = parcel.readString()
        uriType = parcel.readString()
        cacheState = parcel.readInt()
    }

    fun copy(): Attachment {
        val new = Attachment(uri, text, uriType)
        new.id = id
        new.cacheState = cacheState
        return new
    }

    constructor(uri: Uri?, content: String?, type: String) : this() {
        this.uri = uri
        this.text = content.toNotBlank()
        this.uriType = type
    }

    override fun toString() = "Attachment(id=$id, uri=$uri, uriType=$uriType, text='$text')\n"

    fun isEmpty() = uri == null && text.isEmpty()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Attachment

        if (uri != other.uri) return false

        return true
    }

    override fun hashCode() = uri?.hashCode() ?: 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeParcelable(uri, flags)
        parcel.writeString(text)
        parcel.writeString(uriType)
        parcel.writeInt(cacheState)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Attachment> {
        override fun createFromParcel(parcel: Parcel) = Attachment(parcel)

        override fun newArray(size: Int): Array<Attachment?> = arrayOfNulls(size)
    }
}

val NO_NEED_CACHE = 0
val NOT_CACHED = 1
val TEMP_CAHCED = 2
val CACHED = 3