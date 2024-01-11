package com.example.android_radhitiapratama

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.android_radhitiapratama.databinding.ActivityEditBookBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

class EditBookActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditBookBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBookBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        getDetailBook()

        binding.editBookBtnSubmit.setOnClickListener {
            if (!validate()) return@setOnClickListener

            Update()
        }

        binding.editBookBtnBack.setOnClickListener { finish() }
    }

    fun Update() {
        GlobalScope.launch(Dispatchers.IO) {
            val id = intent.getStringExtra("id")
            var conn =
                URL("https://6597231a668d248edf22a0d3.mockapi.io/books/$id").openConnection() as HttpURLConnection
            conn.requestMethod = "PUT"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Accept", "application/json")

            val json = JSONObject().apply {
                put("title", binding.editBookEtTitle.text.toString())
                put("author", binding.editBookEtAuthor.text.toString())
                put("year", binding.editBookEtYear.text.toString().toInt())
                put("pages", binding.editBookEtPages.text.toString().toInt())
                put("country", binding.editBookEtCountry.text.toString())
                put("language", binding.editBookEtLanguage.text.toString())
                put("summary", binding.editBookEtSummary.text.toString())
            }

            val outputStream = DataOutputStream(conn.outputStream)
            outputStream.write(json.toString().toByteArray())
            outputStream.flush()
            outputStream.close()

            val responseCode = conn.responseCode

            runOnUiThread {
                if (responseCode in 200..299) {
                    AlertDialog.Builder(this@EditBookActivity)
                        .setTitle("Success!")
                        .setMessage("Buku berhasil di edit!")
                        .setPositiveButton(
                            "Oke",
                            DialogInterface.OnClickListener { dialog, which -> finish() })
                        .setCancelable(false)
                        .show()
                } else {
                    AlertDialog.Builder(this@EditBookActivity)
                        .setTitle("Gagal!")
                        .setMessage("Buku gagal di hapus!")
                        .setPositiveButton(
                            "Oke",
                            DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                        .setCancelable(false)
                        .show()
                }
            }
        }
    }

    fun getDetailBook() {
        GlobalScope.launch(Dispatchers.IO) {
            val id = intent.getStringExtra("id")
            var conn =
                URL("https://6597231a668d248edf22a0d3.mockapi.io/books/$id").openConnection() as HttpURLConnection
            conn.requestMethod = "GET"

            val result = conn.inputStream.bufferedReader().readText()
            val json = JSONObject(result)

            Log.d("cek_edit", json.toString())

            runOnUiThread {
                binding.editBookEtTitle.setText(json.getString("title").toString())
                binding.editBookEtAuthor.setText(json.getString("author").toString())
                binding.editBookEtCountry.setText(json.getString("country").toString())
                binding.editBookEtLanguage.setText(json.getString("language").toString())
                binding.editBookEtYear.setText(json.getInt("year").toString())
                binding.editBookEtPages.setText(json.getInt("pages").toString())
                binding.editBookEtSummary.setText(json.getString("summary").toString())
            }
        }
    }

    fun validate(): Boolean {
        if (binding.editBookEtTitle.text.toString()
                .isNullOrEmpty() || binding.editBookEtAuthor.text.toString()
                .isNullOrEmpty() || binding.editBookEtCountry.text.toString()
                .isNullOrEmpty() || binding.editBookEtLanguage.text.toString()
                .isNullOrEmpty() || binding.editBookEtYear.text.toString()
                .isNullOrEmpty() || binding.editBookEtPages.text.toString()
                .isNullOrEmpty() || binding.editBookEtSummary.text.toString().isNullOrEmpty()
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