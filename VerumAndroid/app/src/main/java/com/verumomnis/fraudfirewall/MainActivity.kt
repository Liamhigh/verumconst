package com.verumomnis.fraudfirewall

import android.app.Activity
import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var resultBox: TextView
    private var fileHash: String = ""
    private val PICK_FILE_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val uploadBtn = findViewById<Button>(R.id.uploadBtn)
        val pdfBtn = findViewById<Button>(R.id.pdfBtn)
        resultBox = findViewById(R.id.resultBox)

        uploadBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
        }

        pdfBtn.setOnClickListener {
            exportToPdf()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                fileHash = computeSha512(it)
                resultBox.text = "SHA-512 Hash:\\n$fileHash"
            }
        }
    }

    private fun computeSha512(uri: Uri): String {
        val md = MessageDigest.getInstance("SHA-512")
        contentResolver.openInputStream(uri)?.use { stream ->
            val buffer = ByteArray(8192)
            var read: Int
            while (stream.read(buffer).also { read = it } != -1) {
                md.update(buffer, 0, read)
            }
        }
        return md.digest().joinToString("") { "%02x".format(it) }
    }

    private fun exportToPdf() {
        val doc = PdfDocument()
        val page = doc.startPage(PdfDocument.PageInfo.Builder(300, 400, 1).create())
        val canvas = page.canvas
        canvas.drawText("Verum Omnis Report", 10f, 25f, android.graphics.Paint())
        canvas.drawText("Hash:", 10f, 60f, android.graphics.Paint())
        canvas.drawText(fileHash, 10f, 80f, android.graphics.Paint())
        doc.finishPage(page)

        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val fileName = "VerumOmnis_Report_${sdf.format(Date())}.pdf"
        val file = FileOutputStream(File(getExternalFilesDir(null), fileName))
        doc.writeTo(file)
        doc.close()
    }
}
