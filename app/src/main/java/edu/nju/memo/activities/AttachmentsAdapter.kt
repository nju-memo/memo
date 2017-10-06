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
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
            when (position) {
                0 -> renderSummary(summary, holder as SummaryViewHolder)
                else -> renderAttachment(attachments[position - 1], holder as AttachmentViewHolder).
                        takeIf { position == attachments.size }.//the last one, make the editor visible
                        let { holder.textView.visibility = View.VISIBLE }
            }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
            when (viewType) {
                0 -> this::inflateSummary
                else -> this::inflateAttachment
            }(parent)

    override fun getItemCount() = attachments.count() + 1

    override fun getItemViewType(position: Int) = position.coerceAtMost(1)

    private fun disableEdit() {
        if (editable) {
            summaryViewHolder?.textView?.readOnly()
            attachmentViewHolders.forEach { it.textView.readOnly() }
        }
    }

    private fun enableEdit() {
        if (!editable) {
            summaryViewHolder?.textView?.writable()
            attachmentViewHolders.forEach { it.textView.writable() }
        }
    }

    private fun inflateSummary(parent: ViewGroup?) =
            SummaryViewHolder(inflater.inflate(R.layout.layout_summary, parent, false)).
                    also { it.layout.setBackgroundColor(themeColor) }.
                    also { summaryViewHolder = it }

    private fun inflateAttachment(parent: ViewGroup?) =
            AttachmentViewHolder(inflater.inflate(R.layout.layout_attachment, parent, false)).
                    also { attachmentViewHolders.add(it) }

    private fun renderSummary(summary: String,
                              holder: SummaryViewHolder,
                              writable: Boolean = false) =
            with(holder) {
                textView.editable(writable).
                        setText(summary.takeIf { it.isNotBlank() } ?: string(R.string.text_no_summary))
            }

    private fun renderAttachment(attachment: Attachment,
                                 holder: AttachmentViewHolder,
                                 writable: Boolean = false) =
            with(holder) {
                textView.editable(writable)
                if (attachment.text.isBlank()) textView.visibility = View.GONE
                else textView.setText(attachment.text)

                val image = attachment.uri.toFile()?.let { decodeBitmap(it.path) }
                if (image != null) imageView.imageBitmap = image
                else imageView.visibility = View.GONE
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
    var editable = false
        set(value) {
            if (value) enableEdit() else disableEdit()
            field = value
        }
}


class AttachmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var imageView = itemView.child<ImageView>(R.id.image_attachment)
    var textView = itemView.child<EditText>(R.id.edit_attachment)
}

class SummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var layout = itemView as LinearLayout
    var textView = itemView.child<EditText>(R.id.edit_summary)
}
