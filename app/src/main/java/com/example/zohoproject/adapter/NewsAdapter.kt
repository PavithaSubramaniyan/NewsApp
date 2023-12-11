package com.example.zohoproject.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zohoproject.activities.NewsActivity
import com.example.zohoproject.R
import com.example.zohoproject.pojo.NewsItem
import java.util.Locale

class NewsAdapter(private val context: Context) : RecyclerView.Adapter<NewsAdapter.ViewHolder>(), Filterable {

    private var newsList = mutableListOf<NewsItem>()
    private var filteredNewsList = mutableListOf<NewsItem>()


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.newsTitleTextView)
        val summaryTextView: TextView = itemView.findViewById(R.id.newsDetailTextView)
        val imageView: ImageView = itemView.findViewById(R.id.newsThumbnailImageView)
        val readMoreButton: TextView = itemView.findViewById(R.id.readMore_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val news = newsList[position]
        holder.titleTextView.text = news.title
        holder.summaryTextView.text = news.summary

        Log.d("ImageUrl", "  "+news.image_url);
        if (news.image_url != null) {
            Glide.with(context)
                .load(news.image_url)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.imageView)
        } else {

            holder.imageView.setImageResource(R.drawable.ic_launcher_foreground)
        }
        holder.readMoreButton.setOnClickListener {

            val intent = Intent(context, NewsActivity::class.java)
            intent.putExtra("NEWS_URL", news.url)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return newsList.size
    }
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredResults = mutableListOf<NewsItem>()

                if (constraint.isNullOrBlank()) {
                    filteredResults.addAll(newsList)
                } else {
                    val query = constraint.toString().toLowerCase(Locale.getDefault()).trim()

                    val startsWithQuery = mutableListOf<NewsItem>()
                    val otherResults = mutableListOf<NewsItem>()

                    for (item in newsList) {
                        val titleWords = item.title.toLowerCase(Locale.getDefault()).split("\\s+".toRegex())

                        // Check if any word in the title starts with the query
                        if (titleWords.any { it.startsWith(query) }) {
                            startsWithQuery.add(item)
                        } else {
                            otherResults.add(item)
                        }
                    }

                    filteredResults.addAll(startsWithQuery)
                    filteredResults.addAll(otherResults)
                }

                val filterResults = FilterResults()
                filterResults.values = filteredResults
                return filterResults
            }


            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredNewsList.clear()
                filteredNewsList.addAll(results?.values as MutableList<NewsItem>)
                newsList.clear()
                newsList.addAll(filteredNewsList)

                notifyDataSetChanged()
            }
        }
    }


    fun updateData(newData: List<NewsItem>) {
        newsList.addAll(newData)
        filteredNewsList.addAll(newData)
        notifyDataSetChanged()
    }
    fun clearData() {
        newsList.clear()
        filteredNewsList.clear()
        notifyDataSetChanged()
    }

}

