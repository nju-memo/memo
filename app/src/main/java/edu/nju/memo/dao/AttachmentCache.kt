package edu.nju.memo.dao

import edu.nju.memo.domain.Attachment

/**
 * Created by tinker on 2017/9/28.
 */
interface AttachmentCache {
    fun cacheToFile(attachments: Iterable<Attachment>): Boolean

    fun cacheToTemp(attachments: Iterable<Attachment>): Boolean
}