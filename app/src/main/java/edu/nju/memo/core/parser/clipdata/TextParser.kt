package edu.nju.memo.core.parser.clipdata

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import edu.nju.memo.common.safeToString
import edu.nju.memo.domain.Attachment

/**
 * Created by tinker on 2017/9/25.
 */
fun textUriList(item: ClipData.Item) = Attachment(item.uri, item.text.safeToString())

fun textPlain(item: ClipData.Item) = Attachment(null, item.text.safeToString())

fun textHtml(item: ClipData.Item) = Attachment(null, item.htmlText)

fun textIntentURI(item: ClipData.Item) =
        Attachment(Uri.parse(item.intent.toUri(Intent.URI_INTENT_SCHEME)), item.text.safeToString())
