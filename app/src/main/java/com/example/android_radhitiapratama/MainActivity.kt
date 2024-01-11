package com.example.android_radhitiapratama

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_radhitiapratama.adapters.BookAdapter
import com.example.android_radhitiapratama.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileNotFoundException
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.mainBtnAddBook.setOnClickListener {
            val intent = Intent(this@MainActivity, AddBookActivity::class.java)
            startActivity(intent)
        }

        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                getBooks()
            }
        })

        getBooks()
    }

    fun getBooks() {
        GlobalScope.launch(Dispatchers.IO) {
            var conn: HttpURLConnection
            if (binding.searchEt.text.toString().isNullOrEmpty()) {
                conn =
                    URL("https://6597231a668d248edf22a0d3.mockapi.io/books").openConnection() as HttpURLConnection
            } else {
                var textSearch = binding.searchEt.text.toString()
                conn =
                    URL("https://6597231a668d248edf22a0d3.mockapi.io/books?search=$textSearch").openConnection() as HttpURLConnection
            }

            conn.requestMethod = "GET"

            try {
                val result = conn.inputStream.bufferedReader().readText()
                val jsons = JSONArray(result)
                runOnUiThread {
                    binding.booksRecycler.adapter = BookAdapter(jsons)
                    binding.booksRecycler.layoutManager = LinearLayoutManager(applicationContext)
                }
            } catch (ex: FileNotFoundException) {
                runOnUiThread {
                    binding.booksRecycler.adapter = BookAdapter(JSONArray())
                    binding.booksRecycler.layoutManager = LinearLayoutManager(applicationContext)

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getBooks()
    }
}