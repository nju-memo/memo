package edu.nju.memo.domain

/**
 * Class describes the attached content of the MemoItem. It's generated from the Intent#clipData,
 * one for each ClipDataItem.
 *
 * If the content is the same as its parent's content, and the uri is null,
 * then it will be dropped.
 */

class Attachment() {
    var id = 0L
    var uri = ""
    var content = ""

    constructor(uri: String?, content: String?) : this() {
        this.uri = uri ?: ""
        this.content = content ?: ""
    }
}