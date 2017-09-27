package edu.nju.memo.domain

import android.net.Uri
import edu.nju.memo.common.mimeMap
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

    constructor(uri: Uri?, content: String?, type: String) : this() {
        this.uri = uri
        this.content = content.toNotBlank()
        this.type = type.takeIf { mimeMap.hasMimeType(type) } ?: "text/plain"
    }

    override fun toString() = "Attachment(id=$id, uri=$uri, content='$content')"

    fun isEmpty() = uri == null && content.isEmpty()
}