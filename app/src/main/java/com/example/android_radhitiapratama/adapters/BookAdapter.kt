package com.example.android_radhitiapratama.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android_radhitiapratama.DetailBookActivity
import com.example.android_radhitiapratama.R
import org.json.JSONArray

class BookAdapter(val books: JSONArray) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val context = itemView.context
        val title = itemView.findViewById<TextView>(R.id.card_book_title)
        val author = itemView.findViewById<TextView>(R.id.card_book_author)
        val btnDetail = itemView.findViewById<TextView>(R.id.card_book_btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.card_books_layout, parent, false)
        return BookViewHolder(view)
    }

    override fun getItemCount(): Int {
        return books.length()
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books.getJSONObject(position);
        holder.title.text = book.getString("title")
        holder.author.text = book.getString("author")
        holder.btnDetail.setOnClickListener {
            val intent = Intent(holder.context, DetailBookActivity::class.java).apply {
                putExtra("id", book.getString("id"))
            }

            holder.context.startActivity(intent)
        }
    }

}