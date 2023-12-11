package com.example.zohoproject.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.example.zohoproject.R

class NewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val webView: WebView = findViewById(R.id.webView)

        val newsUrl = intent.getStringExtra("NEWS_URL")

        if (newsUrl != null) {
            webView.loadUrl(newsUrl)
        }
    }
}