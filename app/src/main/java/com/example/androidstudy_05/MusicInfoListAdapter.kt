package com.example.androidstudy_05

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudy_05.databinding.MusicInfoRvBinding
import java.text.DecimalFormat

class MusicInfoListAdapter(
    private val onClick: (MusicInfoData) -> Unit
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

    inner class ViewHolder(private val binding: MusicInfoRvBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MusicInfoData) = with(binding) {
            ivMusicImg.setImageResource(data.musicImg)
            tvMusicTitle.text = data.musicTitle
            tvMusicSinger.text = data.musicSinger
            tvMember.text = randomNumber()
            tvHeart.text = randomNumber()
            tvJewel.text = randomNumber()
            tvMusicTime.text = changeMusicTime(data.musicTime)
            musicInfoLayer.setOnClickListener{ onClick(data) }
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

    private fun changeMusicTime(time: Int): String{
        val min = time / 60
        val second = time % 60
        return "${min}분 ${second}초"
    }

    private fun randomNumber(): String{
        val dec = DecimalFormat("#,###")
        return dec.format((0..9999).random())
    }
}