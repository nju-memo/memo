package edu.nju.memo.activities

import android.graphics.drawable.BitmapDrawable
import android.support.annotation.ColorInt
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import edu.nju.memo.R
import edu.nju.memo.common.*
import edu.nju.memo.domain.Attachment
import org.jetbrains.anko.imageBitmap

/**
 * @author [Cleveland Alto](mailto:tinker19981@hotmail.com)
 */
class AttachmentsAdapter(private val summary: String,
                         private val attachments: List<Attachment>,
                         @ColorInt private val themeColor: Int)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) setSummary(summary, holder as SummaryViewHolder)
        else setAttachment(attachments[position - 1], holder as AttachmentViewHolder)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
            if (viewType == 0) renderSummary(parent)
            else renderAttachment(parent)

    fun disableEdit() {
        if (editable) {
            summaryViewHolder?.textView?.readOnly()
            attachmentViewHolders.forEach { it.textView.readOnly() }
            editable = false
        }
    }

    fun enableEdit() {
        if (!editable) {
            summaryViewHolder?.textView?.writable()
            attachmentViewHolders.forEach { it.textView.writable() }
            editable = true
        }
    }

    override fun getItemCount() = attachments.count() + 1

    override fun getItemViewType(position: Int) = position.coerceAtMost(1)

    private fun renderSummary(parent: ViewGroup?) =
            SummaryViewHolder(inflater.inflate(R.layout.layout_summary, parent, false)).
                    also { it.layout.setBackgroundColor(themeColor) }.
                    also { summaryViewHolder = it }

    private fun renderAttachment(parent: ViewGroup?) =
            AttachmentViewHolder(inflater.inflate(R.layout.layout_attachment, parent, false)).
                    also { attachmentViewHolders.add(it) }

    private fun setSummary(summary: String, summaryViewHolder: SummaryViewHolder) {
        summaryViewHolder.textView.editable(editable).
                setText(summary.takeIf { it.isNotBlank() } ?: string(R.string.text_no_summary))
    }

    private fun setAttachment(attachment: Attachment, holder: AttachmentViewHolder) {
        holder.textView.editable(editable).setText(attachment.text)
        attachment.uri.toFile()?.
                let { decodeBitmap(it.path) }?.
                let { holder.imageView.imageBitmap = it }
    }

    fun recycle() {
        summaryViewHolder = null
        attachmentViewHolders.map { it.imageView }.forEach { it.recycleBitmap() }
        attachmentViewHolders.clear()
    }

    private fun ImageView.recycleBitmap() {
        drawable.takeIf { it is BitmapDrawable }?.let { (it as BitmapDrawable).bitmap.recycle() }
    }

    private var summaryViewHolder: SummaryViewHolder? = null
    private val attachmentViewHolders = mutableListOf<AttachmentViewHolder>()
    private val inflater = LayoutInflater.from(application)
    private var editable = true
}


class AttachmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var imageView = itemView.child<ImageView>(R.id.image_attachment)
    var textView = itemView.child<EditText>(R.id.edit_attachment)
}

class SummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var layout = itemView as LinearLayout
    var textView = itemView.child<EditText>(R.id.edit_summary)
}
