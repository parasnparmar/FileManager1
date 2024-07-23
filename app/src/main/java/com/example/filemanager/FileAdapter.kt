package com.example.filemanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FileAdapter(
    private val files: List<File>,
    private val onFileClick: (File) -> Unit
) : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.icon)
        val fileName: TextView = view.findViewById(R.id.fileName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.file_items, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files[position]
        holder.fileName.text = file.name
        holder.icon.setImageResource(
            when {
                file.isDirectory -> R.drawable.baseline_folder_24
                file.extension.equals("pdf", true) -> R.drawable.pdf_icon
                file.extension.equals("mp3", true) || file.extension.equals("wav", true) -> R.drawable.baseline_library_music_24
                file.extension.equals("mp4", true) || file.extension.equals("mkv", true) -> R.drawable.baseline_video_library_24
                else -> R.drawable.baseline_insert_drive_file_24
            }
        )

        holder.itemView.setOnClickListener { onFileClick(file) }
    }

    override fun getItemCount(): Int = files.size
}
