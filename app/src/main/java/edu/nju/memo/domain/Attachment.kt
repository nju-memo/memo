package edu.nju.memo.domain

import android.net.Uri

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

    constructor(uri: Uri?, content: String?) : this() {
        this.uri = uri
        this.content = content ?: ""
    }

    override fun toString() = "Attachment(id=$id, uri=$uri, content='$content')"


}