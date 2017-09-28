package edu.nju.memo.domain

import android.net.Uri
import edu.nju.memo.common.toNotBlank

/**
 * Class describes the attached content of the MemoItem. It's generated from the Intent#clipData,
 * one for each ClipDataItem.
 *
 * If the content is the same as its parent's content, and the uri is null,
 * then it will be dropped.
 */
class Attachment() {
    var id = 0L
    var uri: Uri? = null
    var content = ""
    var type = "text/plain"
    var cacheState = NO_NEED_CACHE

    constructor(uri: Uri?, content: String?, type: String) : this() {
        this.uri = uri
        this.content = content.toNotBlank()
        this.type = type
    }

    override fun toString() = "Attachment(id=$id, uri=$uri, type=$type, content='$content')\n"

    fun isEmpty() = uri == null && content.isEmpty()
}

val NO_NEED_CACHE = 0
val NOT_CACHED = 1
val TEMP_CAHCED = 2
val CACHED = 3