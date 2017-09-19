package edu.nju.memo.core.parser

import android.content.Context
import android.content.Intent
import edu.nju.memo.common.asList
import edu.nju.memo.core.MemoItemFactory

/**
 * Created by tinker on 2017/9/19.
 */
class MimeItemFactory : MemoItemFactory {
    private val mimeParserMap = mapOf<String, List<MimeIntentParser>>(
            "text" to listOf(TextParser())
    )
    private val fallbackParser = FallbackIntentParser()
    private val fallback = fallbackParser.asList()

    override fun parseIntent(intent: Intent, context: Context)
            = chooseParser(intent.type).parseIntent(intent, context)
            .apply { createTime = System.currentTimeMillis() }

    private fun chooseParser(mimeType: String)
            = mimeParserMap.withDefault { fallback }[mimeType.split('/')[0]]!!
            .find { it.canParse(mimeType) } ?: fallbackParser
}