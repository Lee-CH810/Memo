package com.example.flo_clone

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo_clone.databinding.ItemLockerSongBinding

// Recycler View 어댑터
/**
 * ViewHolder 생성 함수
 * ItemView 객체를 만들어 ViewHolder에 넣는다.
 */
class SavedSongRVAdapter(private val songList: ArrayList<Song>) : RecyclerView.Adapter<SavedSongRVAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): SavedSongRVAdapter.ViewHolder {
        val binding: ItemLockerSongBinding = ItemLockerSongBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false)

        return ViewHolder(binding)
    }

    /**
     * ViewHolder 데이터 바인딩 함수
     */
    override fun onBindViewHolder(holder: SavedSongRVAdapter.ViewHolder, position: Int) {

        // songList의 item을 순차적으로 bind
        holder.bind(songList[position])

    }

    /**
     * RecyclerView의 크기 반환
     */
    override fun getItemCount(): Int = songList.size

    inner class ViewHolder(val binding: ItemLockerSongBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Song) {
            binding.itemLockerSongTitleTv.text = song.title
            binding.itemLockerSongSingerTv.text = song.singer
            binding.itemLockerSongCoverIv.setImageResource(song.coverImg!!)
        }
    }
}