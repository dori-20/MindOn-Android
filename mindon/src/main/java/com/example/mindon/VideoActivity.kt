package com.example.mindon

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mindon.RetrofitInstance
import com.example.mindon.YoutubeAdapter
import com.example.mindon.YoutubeResponse
import com.example.mindon.YoutubeVideo
import com.example.mindon.databinding.ActivityVideoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoBinding
    private val videoList = mutableListOf<YoutubeVideo>()
    private lateinit var adapter: YoutubeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        setupRecyclerView()
        setupSearch()

        //키워드
        binding.btnMeditation.setOnClickListener {
            searchYoutube("호흡 명상")
        }
        binding.btnSelfcare.setOnClickListener {
            searchYoutube("기분 좋아지는 플리")
        }
        binding.btnHealing.setOnClickListener {
            searchYoutube("위로 모음")
        }

    }


    private fun setupSearch() {
        binding.searchBtn.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) searchYoutube(query)
        }
    }

    private fun searchYoutube(query: String) {
        val apiKey = "AIzaSyD9YgbQo1wjv6wvpmJx90dGChLJAUVRQ0Y" // 실제 키로 교체

        RetrofitInstance.api.searchVideos(query = query, apiKey = apiKey)
            .enqueue(object : Callback<YoutubeResponse> {
                override fun onResponse(
                    call: Call<YoutubeResponse>,
                    response: Response<YoutubeResponse>
                ) {
                    if (response.isSuccessful) {
                        val results = response.body()?.items?.map {
                            YoutubeVideo(
                                videoId = it.id.videoId,
                                title = it.snippet.title,
                                thumbnailUrl = it.snippet.thumbnails.medium.url
                            )
                        } ?: emptyList()

                        videoList.clear()
                        videoList.addAll(results)
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<YoutubeResponse>, t: Throwable) {
                    Toast.makeText(this@VideoActivity, "API 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setupRecyclerView() {
        adapter = YoutubeAdapter(videoList) { videoId ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }
}