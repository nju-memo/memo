package edu.nju.memo.core.parser.clipdata

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import edu.nju.memo.common.safeToString
import edu.nju.memo.domain.Attachment

/**
 * @author [Cleveland Alto](mailto:tinker19981@hotmail.com)
 */

fun textUriList(item: ClipData.Item) = Attachment(item.uri, item.text.safeToString(), "text/uri-list")

fun textPlain(item: ClipData.Item) = Attachment(null, item.text.safeToString(), "text/plain")

fun textHtml(item: ClipData.Item) = Attachment(null, item.htmlText, "text/html")

fun textIntentURI(item: ClipData.Item) =
        Attachment(Uri.parse(item.intent.toUri(Intent.URI_INTENT_SCHEME)), item.text.safeToString(), "text/vnd.android.intent")
