package edu.nju.memo.common

import java.io.File

/**
 * Created by tinker on 2017/9/28.
 */

fun moveDir(src: File, dst: File): Boolean = (src to dst).
        takeIf { (src, _) -> src.exists() && src.isDirectory }?.
        takeIf { (_, dst) -> dst.exists() || dst.mkdirs() }?.
        let { (src, dst) ->
            src.listFiles().all { (if (it.isDirectory) ::moveDir else ::moveFile)(it, File(dst, it.name)) }
                    && (src.delete() || true)
        } ?: false

fun moveFile(src: File, dst: File) = (src to dst).
        takeIf { (src, _) -> src.isFile }?.
        takeIf { (_, dst) -> dst.isDirectory || (!dst.exists() && dst.mkdirs()) }?.
        let { (src, dst) -> src.renameTo(File(dst, src.name)) } ?: false

