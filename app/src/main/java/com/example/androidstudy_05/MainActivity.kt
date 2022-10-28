package com.example.androidstudy_05

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.example.androidstudy_05.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mp: MediaPlayer
    private val musicInfoListAdapter: MusicInfoListAdapter by lazy {
        MusicInfoListAdapter { data ->
            onMusicClickListener(data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mp = MediaPlayer()

        var uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI
        var selection = MediaStore.Audio.Media.IS_MUSIC
        var sortOrder = MediaStore.Audio.Media.TITLE
        var cursor = contentResolver.query(uri, null, selection, null, sortOrder)
        cursor!!.moveToFirst()

        Log.d("asdf", cursor!!.count.toString())
    }

    private fun onMusicClickListener(data: MusicInfoData) {

    }
}