package edu.nju.memo.domain

import android.net.Uri
import edu.nju.memo.core.MemoItemFactory
import edu.nju.memo.common.toNotBlank
import edu.nju.memo.dao.AttachmentCache

/**
 * Class describes the extra attached content of the MemoItem.
 */
class Attachment() {
    /**
     * Row id in db.
     * */
    var id = 0L
    /**
     * Uri of non-text content.
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
     * Text(plain, html...) content. Not null, empty when absent.
     *
     * (just use WebView)
     * */
    var content = ""
    /**
     * Type.
     *
     * If [uri] is not null, then the type is [uri]'s type. But this doesn't indicate the [content]
     * is absent.
     *
     * If [uri] is absent, this represents [content]'s type
     * */
    var type = "text/plain"
    /**
     * Indicate should cache content from [uri] or not.
     *
     * If [uri] is modified after created via [MemoItemFactory], or initially created manually, please
     * also set this field properly. In detail, if the uri is temporary or expires later, set it to [NOT_CACHED].
     * */
    var cacheState = NO_NEED_CACHE

    constructor(uri: Uri?, content: String?, type: String) : this() {
        this.uri = uri
        this.content = content.toNotBlank()
        this.type = type
    }

    override fun toString() = "Attachment(id=$id, uri=$uri, type=$type, content='$content')\n"

    fun isEmpty() = uri == null && content.isEmpty()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Attachment

        if (uri != other.uri) return false
        if (content != other.content) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uri?.hashCode() ?: 0
        result = 31 * result + content.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}

val NO_NEED_CACHE = 0
val NOT_CACHED = 1
val TEMP_CAHCED = 2
val CACHED = 3