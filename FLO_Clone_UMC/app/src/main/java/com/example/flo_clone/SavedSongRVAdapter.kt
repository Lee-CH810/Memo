package com.example.flo_clone

import android.util.Log
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

    /**
     * ClickListener 객체를 위한 Interface
     * ClickListener 객체에 클릭 이벤트 시 해야 할 작업들을 메서드로 작성할 수 있음.
     */
    interface lockerItemClickListener {
        /** Song 데이터(저장된 곡) 삭제 메서드 */
        fun onRemoveSong(position: Int)
    }

    /** Adapter 내부에서 사용할 ClickListener 변수 선언 */
    private lateinit var myLockerItemClickListener : lockerItemClickListener

    fun removeItem(position: Int){
        songList.removeAt(position) // DB에는 영향 없음
        notifyDataSetChanged()
        Log.d("Flow", "removeItem")
    }

    /**
     * SavedSongRVAdapter에서의 전역변수 myLockerItemClickListener를 초기화하는 함수
     *
     * 외부에서 사용하려면 ClickListener 변수가 필요함
     * 그런데 myLockerItemClickListener를 바로 초기화할 수 없어서 이에 대한 함수가 필요
     */
    fun setMyLockerItemClickListener(itemClickListener: lockerItemClickListener) {
        Log.d("Flow", "setMyLockerItemClickListener")
        myLockerItemClickListener = itemClickListener
    }

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

        /** ViewHoler 클릭 이벤트 처리: more버튼 누르면 삭제 */
        holder.binding.itemLockerSongMoreIv.setOnClickListener{
            Log.d("Flow", "SavedSongRVAdapter onBindViewHolder")
            myLockerItemClickListener.onRemoveSong(position)
        }
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