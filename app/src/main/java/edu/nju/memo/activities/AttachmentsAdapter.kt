package edu.nju.memo.activities

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.support.annotation.ColorInt
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import edu.nju.memo.R
import edu.nju.memo.common.*
import edu.nju.memo.domain.Attachment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.runOnUiThread

/**
 * @author [Cleveland Alto](mailto:tinker19981@hotmail.com)
 */
class AttachmentsAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var mSummary: String
    private lateinit var mTags: List<String>
    private lateinit var mAttachments: List<Attachment>
    private var mThemeColor = color(R.color.colorPrimary)
    private lateinit var mModifiedSummary: String
    private lateinit var mModifiedTags: MutableList<String>
    private lateinit var mModifiedAttachments: MutableList<Attachment>

    constructor(context: Context,
                summary: String,
                tags: List<String>,
                attachments: List<Attachment>,
                @ColorInt themeColor: Int
    ) : this(context) {
        this.mSummary = summary
        this.mTags = tags
        this.mAttachments = attachments
        this.mThemeColor = themeColor
        this.mModifiedSummary = summary
        this.mModifiedTags = mTags.toMutableList()
        this.mModifiedAttachments = attachments.map { it.copy() }.toMutableList()
        mModifiedAttachments.takeIf { it.isEmpty() }?.add(Attachment())
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
                        let { renderSummary(it, editable, overwrite) }
                else -> (holder as AttachmentViewHolder).
                        let { renderAttachment(position - 1, it, editable, overwrite) }
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
            mSummaryViewHolder?.let {
                if (it.textView.text.isBlank()) it.textView.setText(string(R.string.text_no_summary))
            }
            mSummaryViewHolder?.textView?.readOnly()
            mSummaryViewHolder?.let { tagEditable(false, it) }
            attachmentViewHolders.forEach { it.textView.readOnly() }
        }
    }

    private fun enableEdit() {
        if (!editable) {
            if (mModifiedSummary.isBlank()) mSummaryViewHolder?.textView?.setText("")
            mSummaryViewHolder?.textView?.writable()
            mSummaryViewHolder?.let { tagEditable(true, it) }
            attachmentViewHolders.forEach { it.textView.writable() }
        }
    }

    private fun tagEditable(writable: Boolean, holder: SummaryViewHolder) {
        getTagDeleteBtns(holder).forEach {
            it.visible(if (writable) View.VISIBLE else View.GONE)
        }
        holder.tagEdit.visible(if (writable) View.VISIBLE else View.GONE)
    }

    private fun inflateSummary(parent: ViewGroup?): RecyclerView.ViewHolder? {
        mSummaryViewHolder = SummaryViewHolder(inflater.inflate(R.layout.layout_summary, parent, false)).
                also { holder -> holder.layout.setBackgroundColor(mThemeColor) }.
                also { holder -> holder.textView.setText(mModifiedSummary) }.
                also { holder -> mModifiedTags.forEach { addTag(it, holder.tagView) } }.
                also { it.tagEdit.setOnEditorActionListener(addTagListener) }
        return mSummaryViewHolder
    }

    private fun inflateAttachment(parent: ViewGroup?): RecyclerView.ViewHolder? {
        val vh = AttachmentViewHolder(inflater.inflate(R.layout.layout_attachment, parent, false))
        attachmentViewHolders.add(vh)
        return vh
    }

    private fun renderSummary(holder: SummaryViewHolder,
                              writable: Boolean = false,
                              overwrite: Boolean = true) =
            holder.apply {
                if (overwrite) saveSummaryState(holder)

                textView.editable(writable)
                        .setText(mModifiedSummary.takeIf { writable || it.isNotBlank() } ?: string(R.string.text_no_summary))
                tagEditable(writable, holder)
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

                doAsync {
                    val image = attachment.uri.toFile()?.let { decodeBitmap(it.path) }

                    context.runOnUiThread {
                        if (image != null) imageView.imageBitmap = image
                        else imageView.visible(View.GONE)
                    }
                }

                setPosition(position)
            }

    private fun addTag(tag: String, container: FlowLayout) {
        val newTagLayout = inflater.inflate(R.layout.layout_tag, container, false)
        newTagLayout.child<TextView>(R.id.text_tag).text = tag
        newTagLayout.child<ImageButton>(R.id.btn_delete).
                apply {
                    visible(if (editable) View.VISIBLE else View.GONE)
                    setOnClickListener(deleteTagListener)
                }
        container.addView(newTagLayout)
    }

    private fun isNewTag(v: TextView?, actionId: Int)
            = actionId == EditorInfo.IME_ACTION_DONE && v is EditText && v.text.isNotEmpty()


    private fun saveSummaryState(holder: SummaryViewHolder) {
        mModifiedSummary = holder.textView.text.toString()
        mModifiedTags = getTagViews(holder).map { it.text.toString() }.toMutableList()
    }

    private fun saveAttachmentState(holder: AttachmentViewHolder) {
        holder.textView.getTag<Int>(positionTag)?.let {
            mModifiedAttachments[it].apply { text = holder.textView.text.toString() }
        }
    }

    private fun getTagViews(holder: SummaryViewHolder) =
            holder.tagView.
                    let {
                        it.sequence(it.childCount, ViewGroup::getChildAt).
                                map { it.child<TextView>(R.id.text_tag) }.toList()
                    }

    private fun getTagDeleteBtns(holder: SummaryViewHolder) =
            holder.tagView.
                    let {
                        it.sequence(it.childCount, ViewGroup::getChildAt).
                                map { it.child<ImageView>(R.id.btn_delete) }.toList()
                    }

    fun recycle() {
        mSummaryViewHolder = null
        attachmentViewHolders.map { it.imageView }.forEach { it.recycleBitmap() }
        attachmentViewHolders.clear()
    }

    private fun ImageView.recycleBitmap() {
        drawable.takeIf { it is BitmapDrawable }?.let { (it as BitmapDrawable).bitmap.recycle() }
    }

//    private fun deleteAttachmentAt(position: Int) {
//        val removed = mModifiedAttachments.removeAt(position)
//        if (mModifiedAttachments.isEmpty()) {
//            mModifiedAttachments.add(Attachment())
//            if (!removed.isEmpty())
//                notifyItemChanged(position + 1, "payload")
//        } else {
//            notifyItemRemoved(position + 1)
//            notifyItemRangeChanged(position + 1, itemCount - position - 1, "payload")
//        }
//        attachmentViewHolders.
//                find { it.textView.getTag<Int>(positionTag) == (position - 1).coerceAtLeast(0) }?.
//                textView?.requestFocus()
//    }

//    private fun isDeletion(v: View, keyCode: Int, event: KeyEvent)
//            = v is EditText && event.action == KeyEvent.ACTION_UP &&
//            keyCode == KeyEvent.KEYCODE_DEL && v.text.isBlank()

    private var mSummaryViewHolder: SummaryViewHolder? = null
    private val attachmentViewHolders = mutableListOf<AttachmentViewHolder>()
    private val inflater = LayoutInflater.from(application)
    private val deleteTagListener = { v: View ->
        if (v is ImageButton) mSummaryViewHolder?.tagView?.removeView(v.parent as View)
    }
    private val addTagListener = { v: TextView?, actionId: Int, event: KeyEvent? ->
        if (isNewTag(v, actionId)) {
            addTag(v!!.text.toString(), mSummaryViewHolder!!.tagView)
            v.text = ""
            true
        } else false
    }
    //    private val attachmentDeletionListener = { v: View, keyCode: Int, event: KeyEvent ->
//        isDeletion(v, keyCode, event).
//                ifTrue { v.getTag<Int>(positionTag)?.let { deleteAttachmentAt(it) } }
//    }
    var editable = false
        set(value) {
            if (value) enableEdit() else disableEdit()
            field = value
        }

    fun save(): Triple<String, MutableList<String>, MutableList<Attachment>> {
        mSummaryViewHolder?.let { saveSummaryState(it) }
        attachmentViewHolders.forEach { saveAttachmentState(it) }
        return Triple(mModifiedSummary, mModifiedTags, mModifiedAttachments)
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
    var tagView = itemView.child<FlowLayout>(R.id.layout_tag)
    var tagEdit = itemView.child<EditText>(R.id.edit_new_tag)
}
