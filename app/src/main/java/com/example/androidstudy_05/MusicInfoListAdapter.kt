package com.example.androidstudy_05

import android.content.ContentUris
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidstudy_05.databinding.MusicInfoRvBinding
import java.text.DecimalFormat

class MusicInfoListAdapter(
    private val onClick: (MusicInfoData, Int) -> Unit
) :
    ListAdapter<MusicInfoData, MusicInfoListAdapter.ViewHolder>(DIFF_CALLBACK) {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MusicInfoData>() {
            override fun areItemsTheSame(oldItem: MusicInfoData, newItem: MusicInfoData): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: MusicInfoData, newItem: MusicInfoData): Boolean {
                return true
            }
        }
    }

    private var artworkUri = Uri.parse("content://media/external/audio/albumart")

    inner class ViewHolder(private val binding: MusicInfoRvBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MusicInfoData) = with(binding) {
            var albumUri = ContentUris.withAppendedId(artworkUri, data.musicImg)
            Log.i("qwe123", "albumUri: $albumUri")
            Glide.with(itemView.context).load(albumUri).thumbnail(0.1f).placeholder(R.color.white).into(ivMusicImg)
            val title = if(data.musicTitle.length >= 30) "${data.musicTitle.substring(0, 30)}..."
                        else data.musicTitle
            val singer = if (data.musicSinger.length >= 30) "${data.musicSinger.substring(0, 30)}..."
                        else data.musicSinger
            tvMusicTitle.text = title
            tvMusicSinger.text = singer
            tvMember.text = randomNumber()
            tvHeart.text = randomNumber()
            tvJewel.text = randomNumber()
            tvMusicTime.text = changeMusicTime(data.musicTime)
            musicInfoLayer.setOnClickListener{ onClick(data, adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = MusicInfoRvBinding.bind(layoutInflater.inflate(R.layout.music_info_rv, parent, false))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    private fun changeMusicTime(time: Long): String{
        val min = time / 1000 / 60
        val second = time / 1000 % 60
        return "${min}분 ${second}초"
    }

    private fun randomNumber(): String{
        val dec = DecimalFormat("#,###")
        return dec.format((0..9999).random())
    }
}