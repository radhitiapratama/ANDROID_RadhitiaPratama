package com.example.android_radhitiapratama

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.android_radhitiapratama.databinding.ActivityAddBookBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

class AddBookActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddBookBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.addBookBtnSubmit.setOnClickListener {
            if (!validate()) return@setOnClickListener

            addBook()
        }

        binding.addBookBtnBack.setOnClickListener { finish() }
    }

    fun addBook() {
        GlobalScope.launch(Dispatchers.IO) {
            val conn =
                URL("https://6597231a668d248edf22a0d3.mockapi.io/books").openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Accept", "application/json")
            conn.doOutput = true

            val json = JSONObject().apply {
                put("title", binding.addBookEtTitle.text.toString())
                put("author", binding.addBookEtAuthor.text.toString())
                put("year", binding.addBookEtYear.text.toString().toInt())
                put("pages", binding.addBookEtPages.text.toString().toInt())
                put("country", binding.addBookEtCountry.text.toString())
                put("language", binding.addBookEtLanguage.text.toString())
                put("summary", binding.addBookEtSummary.text.toString())
            }

            val outputStream = DataOutputStream(conn.outputStream)
            outputStream.write(json.toString().toByteArray())
            outputStream.flush()
            outputStream.close()

            val statusCode = conn.responseCode

            runOnUiThread {
                if (statusCode in 200..299) {
                    AlertDialog.Builder(this@AddBookActivity)
                        .setCancelable(false)
                        .setTitle("Success!")
                        .setMessage("Buku berhasil di tambahkan!")
                        .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                            finish()
                        })
                        .show()
                } else {
                    AlertDialog.Builder(this@AddBookActivity)
                        .setCancelable(false)
                        .setTitle("Gagal!")
                        .setMessage("Buku gagal di tambahkan silahkan coba lagi")
                        .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                        })
                        .show()
                }
            }
        }
    }

    fun validate(): Boolean {
        if (binding.addBookEtTitle.text.toString()
                .isNullOrEmpty() || binding.addBookEtAuthor.text.toString()
                .isNullOrEmpty() || binding.addBookEtCountry.text.toString()
                .isNullOrEmpty() || binding.addBookEtLanguage.text.toString()
                .isNullOrEmpty() || binding.addBookEtYear.text.toString()
                .isNullOrEmpty() || binding.addBookEtPages.text.toString()
                .isNullOrEmpty() || binding.addBookEtSummary.text.toString().isNullOrEmpty()
        ) {
            AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Gagal!")
                .setMessage("Semua input wajib di isi!")
                .setNeutralButton(
                    "OK",
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                .show()
            return false
        }

        return true
    }
}