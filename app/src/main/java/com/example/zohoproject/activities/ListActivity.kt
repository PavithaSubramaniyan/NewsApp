package com.example.zohoproject.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zohoproject.R
import com.example.zohoproject.adapter.NewsAdapter
import com.example.zohoproject.apiService.NewsRetroService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListActivity : AppCompatActivity() {

    private lateinit var newsAdapter: NewsAdapter
    private var isLoading = false
    private var currentPage = 0
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        newsAdapter = NewsAdapter(this)
        recyclerView.adapter = newsAdapter
        progressBar = findViewById(R.id.progressBar)
        searchView = findViewById(R.id.searchView)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD)) {
                    isLoading = true
                    currentPage++
                    loadNews()
                }
            }
        })
        loadNews()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("SearchView", "  msg");
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("Filter", "Query Text: $newText")
                newsAdapter.filter.filter(newText)
                return true
            }
        })


    }

   private fun loadNews() {
       progressBar.visibility = View.VISIBLE
       CoroutineScope(Dispatchers.Main).launch {
           try {
               val newsResponse = NewsRetroService.newsApi.getNews(limit = PAGE_SIZE, offset = currentPage * PAGE_SIZE)
               Log.d("NewResponse", "  $newsResponse");
               if (newsResponse != null) {
                   val news = newsResponse.results
                   if (news != null) {
                       newsAdapter.clearData()
                       newsAdapter.updateData(news)
                   }
               }
           } catch (e: Exception) {
               e.printStackTrace()
           } finally {
               isLoading = false
               progressBar.visibility = View.GONE
           }
       }
   }

    companion object {
        private const val VISIBLE_THRESHOLD = 5
        private const val PAGE_SIZE = 20
    }
}