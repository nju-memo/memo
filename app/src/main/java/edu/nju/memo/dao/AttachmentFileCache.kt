package edu.nju.memo.dao

import android.content.Intent
import android.net.Uri
import edu.nju.memo.common.*
import edu.nju.memo.domain.Attachment
import edu.nju.memo.domain.CACHED
import edu.nju.memo.domain.NOT_CACHED
import edu.nju.memo.domain.TEMP_CAHCED
import java.io.*
import java.net.URI

/**
 * Created by tinker on 2017/9/28.
 */
object AttachmentFileCache : AttachmentCache {
    private val fileDir = application.filesDir
    private val cacheDir = application.cacheDir

    override fun cacheToTemp(attachments: Iterable<Attachment>) =
            attachments.
                    filter { it.cacheState == NOT_CACHED && it.uri != null }.
                    map { File(cacheDir, "${it.uri!!.hashCode()}") to it }.
                    all { (file, attachment) ->
                        save(file.outputStream(), attachment.uri!!).ifTrue {
                            attachment.cacheState = TEMP_CAHCED
                            attachment.uri = Uri.fromFile(file)
                        }
                    }

    override fun cacheToFile(attachments: Iterable<Attachment>) =
            attachments.takeIf { cacheToTemp(it) }?.
                    filter { it.cacheState == TEMP_CAHCED }?.
                    map { File(URI(it.uri.safeToString())) to it }?.
                    all { (file, attachment) ->
                        moveFile(file, fileDir).ifTrue {
                            attachment.cacheState = CACHED
                            attachment.uri = Uri.fromFile(File(fileDir, file.name))
                        }
                    } ?: false

    fun deleteCache(attachments: Iterable<Attachment>) =
            attachments.
                    filter { it.cacheState == CACHED && it.uri != null }.
                    map { File(URI(it.uri.safeToString())) }.
                    all { it.isFile && it.delete() }

    private fun save(outputStream: OutputStream, uri: Uri) = outputStream.use { output ->
        application.grantUriPermission(application.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

        application.contentResolver.openInputStream(uri).use { input -> retrySave(output, input) }
    }

    private val THRESHOLD = 5
    private fun retrySave(outputStream: OutputStream, inputStream: InputStream, time: Int = 0): Boolean
            = try {
        inputStream.copyTo(outputStream)
        true
    } catch (ex: IOException) {
        if (time < THRESHOLD) retrySave(outputStream, inputStream, time + 1)
        else false
    }
}