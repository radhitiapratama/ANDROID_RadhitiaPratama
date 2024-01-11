package com.example.android_radhitiapratama

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android_radhitiapratama.adapters.BookAdapter
import com.example.android_radhitiapratama.databinding.ActivityDetailBookBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileNotFoundException
import java.net.HttpURLConnection
import java.net.URL

class DetailBookActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailBookBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBookBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        getBookDetails()

        binding.bookDetailBtnEdit.setOnClickListener {
            val intent = Intent(this@DetailBookActivity, EditBookActivity::class.java).apply {
                putExtra("id", intent.getStringExtra("id"))
            }

            startActivity(intent)
        }

        binding.bookDetailBtnBack.setOnClickListener { finish() }

        binding.bookDetailBtnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Peringatan!")
                .setMessage("Apakah anda yakin ingin menghapus buku?")
                .setPositiveButton("Iya", DialogInterface.OnClickListener { dialog, which ->
                    deleteBook()
                })
                .setNegativeButton(
                    "Batal",
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        getBookDetails()
    }

    fun deleteBook() {
        GlobalScope.launch(Dispatchers.IO) {
            val id = intent.getStringExtra("id")
            var conn =
                URL("https://6597231a668d248edf22a0d3.mockapi.io/books/$id").openConnection() as HttpURLConnection
            conn.requestMethod = "DELETE"

            val responseCode = conn.responseCode
            runOnUiThread {
                if (responseCode in 200..299) {
                    AlertDialog.Builder(this@DetailBookActivity)
                        .setTitle("Success!")
                        .setMessage("Buku berhasil di hapus!")
                        .setPositiveButton(
                            "Oke",
                            DialogInterface.OnClickListener { dialog, which -> finish() })
                        .setCancelable(false)
                        .show()
                } else {
                    AlertDialog.Builder(this@DetailBookActivity)
                        .setTitle("Gagal!")
                        .setMessage("Gagal menghapus buku")
                        .setPositiveButton(
                            "Oke",
                            DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                        .setCancelable(false)
                        .show()
                }
            }
        }
    }

    fun getBookDetails() {
        GlobalScope.launch(Dispatchers.IO) {
            val id = intent.getStringExtra("id")
            var conn =
                URL("https://6597231a668d248edf22a0d3.mockapi.io/books/$id").openConnection() as HttpURLConnection
            conn.requestMethod = "GET"

            try {
                val result = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(result)

                runOnUiThread {
                    binding.bookDetailTitle.text = json.getString("title").toString()
                    binding.bookDetailAuthor.text = json.getString("author").toString()
                    binding.bookDetailYear.text = json.getString("year").toString()
                    binding.bookDetailPages.text = json.getString("pages").toString()
                    binding.bookDetailCountry.text = json.getString("country").toString()
                    binding.bookDetailLanguage.text = json.getString("language").toString()
                    binding.bookDetailSummary.text = json.getString("summary").toString()
                }
            } catch (ex: FileNotFoundException) {
                runOnUiThread {
                    finish()
                }
            }

        }
    }
}