package com.example.filemanager

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filemanager.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            loadFiles()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        if (allPermissionsGranted()) {
            loadFiles()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun allPermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun loadFiles() {
        val path = intent.getStringExtra("path") ?: Environment.getExternalStorageDirectory().path
        val directory = File(path)
        if (directory.exists()) {
            val files = directory.listFiles()?.toList() ?: emptyList()
            binding.recyclerView.adapter = FileAdapter(files) { file ->
                if (file.isDirectory) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("path", file.path)
                    startActivity(intent)
                } else {
                    openFile(file)
                }
            }
        } else {
            Toast.makeText(this, "Directory not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openFile(file: File) {
        val uri: Uri = FileProvider.getUriForFile(this, "com.example.filemanager.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri, getMimeType(file.extension))
        startActivity(Intent.createChooser(intent, "Open with"))
    }

    private fun getMimeType(extension: String): String {
        return when (extension) {
            "mp3", "wav" -> "audio/*"
            "txt" -> "text/*"
            "mp4", "mkv" -> "video/*"
            "pdf" -> "application/pdf"
            else -> "*/*"
        }
    }
}
