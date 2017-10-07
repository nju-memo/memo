package edu.nju.memo.activities

import android.graphics.drawable.BitmapDrawable
import android.support.annotation.ColorInt
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
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
class AttachmentsAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var mSummary: String
    private lateinit var mAttachments: List<Attachment>
    private var mThemeColor = color(R.color.colorPrimary)
    private lateinit var mModifiedSummary: String
    private lateinit var mModifiedAttachments: MutableList<Attachment>

    constructor(summary: String,
                attachments: List<Attachment>,
                @ColorInt themeColor: Int
    ) : this() {
        this.mSummary = summary
        this.mAttachments = attachments
        this.mThemeColor = themeColor
        this.mModifiedSummary = summary
        this.mModifiedAttachments = attachments.map { it.copy() }.toMutableList()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) bindViewHolder(holder, position, true)
        else bindViewHolder(holder, position, false)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
            bindViewHolder(holder, position, true)


    private fun bindViewHolder(holder: RecyclerView.ViewHolder, position: Int, overwrite: Boolean) =
            when (position) {
                0 -> (holder as SummaryViewHolder).
                        let {
                            renderSummary(it, editable, overwrite)
                        }
                else -> (holder as AttachmentViewHolder).
                        let {
                            renderAttachment(position - 1, it, editable, overwrite)
                        }
            }.let { Unit }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
            when (viewType) {
                0 -> this::inflateSummary
                else -> this::inflateAttachment
            }(parent)

    override fun getItemCount() = mModifiedAttachments.count() + 1

    override fun getItemViewType(position: Int) = position.coerceAtMost(1)

    private fun disableEdit() {
        if (editable) {
            mSummaryViewHolder?.textView?.readOnly()
            attachmentViewHolders.forEach { it.textView.readOnly() }
        }
    }

    private fun enableEdit() {
        if (!editable) {
            mSummaryViewHolder?.textView?.writable()
            attachmentViewHolders.forEach { it.textView.writable() }
        }
    }

    private fun inflateSummary(parent: ViewGroup?) =
            SummaryViewHolder(inflater.inflate(R.layout.layout_summary, parent, false)).
                    also { it.layout.setBackgroundColor(mThemeColor) }.
                    also { it.textView.setText(mModifiedSummary) }.
                    also { mSummaryViewHolder = it }

    private fun inflateAttachment(parent: ViewGroup?) =
            AttachmentViewHolder(inflater.inflate(R.layout.layout_attachment, parent, false)).
                    also { it.textView.setOnKeyListener(attachmentDeletionListener) }.
                    also { it.textView.setBackgroundColor(color(R.color.black_transparent)) }.
                    also { attachmentViewHolders.add(it) }

    private fun renderSummary(holder: SummaryViewHolder,
                              writable: Boolean = false,
                              overwrite: Boolean = true) =
            holder.apply {
                if (overwrite) saveSummaryState(holder)
                textView.editable(writable).
                        setText(mModifiedSummary.takeIf { it.isNotBlank() } ?: string(R.string.text_no_summary))
            }

    private fun renderAttachment(position: Int,
                                 holder: AttachmentViewHolder,
                                 writable: Boolean = false,
                                 overwrite: Boolean = true) =
            holder.apply {
                if (overwrite) saveAttachmentState(holder)

                val attachment = mModifiedAttachments[position]
                textView.editable(writable)
                if (attachment.text.isBlank() && position != mModifiedAttachments.lastIndex)
                    textView.visible(View.GONE)
                else textView.setText(attachment.text)

                val image = attachment.uri.toFile()?.let { decodeBitmap(it.path) }
                if (image != null) imageView.imageBitmap = image
                else imageView.visible(View.GONE)

                setPosition(position)
            }

    private fun saveSummaryState(holder: SummaryViewHolder) {
        mModifiedSummary = holder.textView.text.toString()
    }

    private fun saveAttachmentState(holder: AttachmentViewHolder) {
        holder.textView.getTag<Int>(positionTag)?.let {
            mModifiedAttachments[it].apply {
                text = holder.textView.text.toString()
            }
        }
    }

    fun recycle() {
        mSummaryViewHolder = null
        attachmentViewHolders.map { it.imageView }.forEach { it.recycleBitmap() }
        attachmentViewHolders.clear()
    }

    private fun ImageView.recycleBitmap() {
        drawable.takeIf { it is BitmapDrawable }?.let { (it as BitmapDrawable).bitmap.recycle() }
    }

    private fun deleteAttachmentAt(position: Int) {
        val removed = mModifiedAttachments.removeAt(position)
        if (mModifiedAttachments.isEmpty()) {
            mModifiedAttachments.add(Attachment())
            if (!removed.isEmpty())
                notifyItemChanged(position + 1, "payload")
        } else {
            notifyItemRemoved(position + 1)
            notifyItemRangeChanged(position + 1, itemCount - position - 1, "payload")
        }
        attachmentViewHolders.
                find { it.textView.getTag<Int>(positionTag) == (position - 1).coerceAtLeast(0) }?.
                textView?.requestFocus()
    }

    private fun isDeletion(v: View, keyCode: Int, event: KeyEvent)
            = v is EditText && event.action == KeyEvent.ACTION_UP &&
            keyCode == KeyEvent.KEYCODE_DEL && v.text.isBlank()

    private var mSummaryViewHolder: SummaryViewHolder? = null
    private val attachmentViewHolders = mutableListOf<AttachmentViewHolder>()
    private val inflater = LayoutInflater.from(application)
    private val attachmentDeletionListener = { v: View, keyCode: Int, event: KeyEvent ->
        isDeletion(v, keyCode, event).
                ifTrue { v.getTag<Int>(positionTag)?.let { deleteAttachmentAt(it) } }
    }
    var editable = false
        set(value) {
            if (value) enableEdit() else disableEdit()
            field = value
        }

    inner class Editor {
        fun save(): Pair<String, MutableList<Attachment>> {
            mSummaryViewHolder?.let { saveSummaryState(it) }
            attachmentViewHolders.forEach { saveAttachmentState(it) }
            return mModifiedSummary to mModifiedAttachments
        }
    }
}


class AttachmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var imageView = itemView.child<ImageView>(R.id.image_attachment)
    var textView = itemView.child<EditText>(R.id.edit_attachment)
    fun setPosition(position: Int): AttachmentViewHolder {
        textView.setTag(positionTag, position)
        return this
    }
}

class SummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var layout = itemView as LinearLayout
    var textView = itemView.child<EditText>(R.id.edit_summary)
}
