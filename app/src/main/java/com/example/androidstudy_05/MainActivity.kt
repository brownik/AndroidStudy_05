package com.example.androidstudy_05

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PowerManager
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.isGone
import com.airbnb.lottie.LottieAnimationView
import com.example.androidstudy_05.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mediaPlayer: MediaPlayer
    private var list = mutableListOf<MusicInfoData>()
    private val musicInfoListAdapter: MusicInfoListAdapter by lazy {
        MusicInfoListAdapter { data, position ->
            onMusicClickListener(data, position)
        }
    }
    private var currentPlayPosition: Int? = null
    private lateinit var makeView: MakeView
    private lateinit var playingLottie: LottieAnimationView

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mediaPlayer = MediaPlayer()
        mediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK)
        makeView = MakeView(this)
        playingLottie = makeView.makeLottieView()

        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
            Context.MODE_PRIVATE
        )

        addOnClickListener()

        var uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var selection = MediaStore.Audio.Media.IS_MUSIC
        var sortOrder = MediaStore.Audio.Media.TITLE
        var cursor = contentResolver.query(uri, null, selection, null, sortOrder)
        cursor!!.moveToFirst()

        Log.d("asdf", cursor!!.count.toString())
        if (cursor != null && cursor.count > 0) {
            do {
                var musicImg =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID))
                var musicTitle =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                var musicSinger = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                var musicTime =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                var musicPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                list.add(MusicInfoData(musicImg, musicTitle, musicSinger, musicTime, musicPath))
                Log.d("asdf",
                    "thumbnail = ${musicImg}, title : ${musicTitle}, artist : ${musicSinger}, 총시간 : ${musicTime}, data : ${musicPath}")
            } while (cursor.moveToNext())
        }
        cursor.close()

        binding.rvOptionList.adapter = musicInfoListAdapter
        musicInfoListAdapter.submitList(list)
    }

    override fun onPause() {
        if (mediaPlayer.isPlaying) stopPlayer(isPause = true)
        super.onPause()
    }

    private fun addOnClickListener() = with(binding) {
        floatingBar.setOnClickListener {}

        // 일시정지 또는 시작 버튼
        btnPlayingChange.setOnClickListener {
            if (mediaPlayer.isPlaying) stopPlayer(isPause = true)
            else startPlayer()
        }

        // 이전 버튼
        btnPlayingPrev.setOnClickListener {
            mediaPlayer.stop()
            mediaPlayer.reset()
            currentPlayPosition =
                if (currentPlayPosition == 0) list.size - 1 else currentPlayPosition!! - 1
            mediaPlayer.setDataSource(list[currentPlayPosition!!].musicPath)
            mediaPlayer.prepare()
            mediaPlayer.start()
            makeSeekBar(list[currentPlayPosition!!])
            setFloatingBar(list[currentPlayPosition!!])
        }

        // 다음 버튼
        btnPlayingNext.setOnClickListener {
            mediaPlayer.stop()
            mediaPlayer.reset()
            currentPlayPosition =
                if (currentPlayPosition == list.size - 1) 0 else currentPlayPosition!! + 1
            mediaPlayer.setDataSource(list[currentPlayPosition!!].musicPath)
            mediaPlayer.prepare()
            mediaPlayer.start()
            makeSeekBar(list[currentPlayPosition!!])
            setFloatingBar(list[currentPlayPosition!!])
        }
    }

    private fun startPlayer() = with(binding) {
        btnPlayingChange.setBackgroundResource(R.drawable.btn_tape_stop)
        flPlayingLottie.removeView(playingLottie)
        flPlayingLottie.addView(playingLottie)
        flPlayingLottie.isGone = false
        mediaPlayer.start()
    }

    private fun stopPlayer(isPause: Boolean) = with(binding) {
        btnPlayingChange.setBackgroundResource(R.drawable.btn_tape_play)
        flPlayingLottie.removeView(playingLottie)
        flPlayingLottie.isGone = true
        if (isPause) mediaPlayer.pause()
        else mediaPlayer.stop()
    }

    private fun onMusicClickListener(data: MusicInfoData, position: Int) = with(binding) {
        if (!mediaPlayer.isPlaying) {
            // Floating Bar 올라오기 Animation
            var anim = appearFloatingBar(floatingBar)
            anim.start()
            // 일시정지 버튼 활성화
            btnPlayingChange.setBackgroundResource(R.drawable.btn_tape_stop)
            // 음악 재생 중 로티 활성화
            flPlayingLottie.removeView(playingLottie)
            flPlayingLottie.addView(playingLottie)
            flPlayingLottie.isGone = false
        }
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.setDataSource(data.musicPath)
        mediaPlayer.prepare()
        mediaPlayer.start()
        makeSeekBar(data)
        currentPlayPosition = position
        setFloatingBar(data)
    }

    private fun appearFloatingBar(layout: ConstraintLayout): ObjectAnimator {
        return ObjectAnimator.ofFloat(layout, View.TRANSLATION_Y, -layout.height.toFloat()).apply {
            duration = 1000L
        }
    }

    private fun setFloatingBar(data: MusicInfoData) = with(binding) {
        val title = if (data.musicTitle.length >= 20) "${data.musicTitle.substring(0, 20)}..."
        else data.musicTitle
        val singer = if (data.musicSinger.length >= 20) "${data.musicSinger.substring(0, 20)}..."
        else data.musicSinger
        tvPlayingTitle.text = title
        tvPlayingSinger.text = singer
    }

    private fun makeSeekBar(data: MusicInfoData) = with(binding) {
        object : Thread() {
            override fun run() {
                super.run()
                if (mediaPlayer == null) return
                flSeekBar.max = data.musicTime.toInt()
                while (mediaPlayer.isPlaying) {
                    runOnUiThread {
                        flSeekBar.progress = mediaPlayer.currentPosition
                    }
                    SystemClock.sleep(200)
                }
                if (!mediaPlayer.isPlaying) {
                    mediaPlayer.stop()      //음악 정지
                    mediaPlayer.reset()
                }
            }
        }.start()
    }

}