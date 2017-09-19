package edu.nju.memo.domain

/**
 * Created by tinker on 2017/9/19.
 */
class Attachment() {
    var uri = ""
    var content = ""

    constructor(uri: String?, content: String?) : this() {
        this.uri = uri ?: ""
        this.content = uri ?: ""
    }
}