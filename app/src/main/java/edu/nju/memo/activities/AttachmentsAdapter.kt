package edu.nju.memo.activities

import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import edu.nju.memo.R
import edu.nju.memo.common.application
import edu.nju.memo.common.child
import edu.nju.memo.common.toFilePath
import edu.nju.memo.domain.Attachment

/**
 * @author [Cleveland Alto](mailto:tinker19981@hotmail.com)
 */
class AttachmentsAdapter(val attachments: List<Attachment>) : RecyclerView.Adapter<AttachmentViewHolder>() {
    override fun onBindViewHolder(holder: AttachmentViewHolder?, position: Int) {
        with(attachments[position]) {
            holder?.imageView?.setImageBitmap(BitmapFactory.decodeFile(uri.toFilePath()))
            holder?.textView?.text = text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
            inflater.inflate(R.layout.view_attachment, parent, false).let { AttachmentViewHolder(it) }

    override fun getItemCount() = attachments.count()

    private val inflater = LayoutInflater.from(application)
}

class AttachmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var imageView = itemView.child<ImageView>(R.id.image_attachment)
    var textView = itemView.child<TextView>(R.id.text_attachment)
}
