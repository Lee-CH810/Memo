package com.example.flo_clone

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo_clone.databinding.ItemAlbumBinding

// Recycler View 어댑터
// item view에 데이터를 바인딩해주기 위해서는 데이터 리스트를 매개변수로 받아야 함.
// Recycler View.Adapter의 상속이 필요한데, 이때 ViewHoler가 필요함. ViewHolder를 아직 만들지 않아서 오류가 생기지만, 일단 작성하고 ViewHolder를 생성
class AlbumRVAdapter(private val albumList: ArrayList<Album>) : RecyclerView.Adapter<AlbumRVAdapter.ViewHolder>() {

    /**
     * Adapter 클래스 범주 안에서만의 클릭 이벤트 처리가 아닌,
     * Adapter의 외부, Activity나 Fragment 등에서 아이템 클릭 이벤트를 처리하고자 할 때,
     * RecyclerView에는 클릭 리스너 자체가 내장되어 있지 않기 때문에, 추가로 클릭 리스너 역할을 하는 인터페이스를 만들어 주어야 함
     */
    interface MyItemClickListener {
        /**
         * item을 클릭했을 때를 의미하는 함수
         * @param album item view 클릭 시 Fragment 전환과 함께 데이터 전달도 하기 위해서 매개변수로 album을 전달
         * Fragment 전환은 HomeFragment에서 override
         */
        fun onItemClick(album: Album)
    }

    /**
     * 외부에서 클릭 이벤트를 사용하기 위해서 외부에서 listener 객체를 넘겨 받아야 함.
     * 외부에서 listener 객체를 전달받는 함수(setMyItemClickListener)와
     * 전달받은 listener 객체를 adapter에서 사용할 수 있도록 따로 저장할 변수(mItemClickListener)를 선언해주어야 함.
     * adapter 외부인 HomeFragment에서 listener 객체를 넘겨주면 adapter에서 이를 받아서 사용
     */
    private lateinit var mItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    /**
     * ViewHolder를 생성해주어야 할 때 호출되는 함수.
     * ItemView 객체를 만든 후에, 재활용을 위해 ViewHolder에 던져주는 작업
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AlbumRVAdapter.ViewHolder {
        // ItemView 객체를 만듦
        val binding: ItemAlbumBinding = ItemAlbumBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        // 만든 item 객체를 ViewHolder에 던져주고 있음.
        return ViewHolder(binding)
    }

    /**
     * ViewHolder에 데이터를 바인딩해주는 작업을 위해 호출되는 함수.
     * 재활용의 개념으로 인해 조금만 생성되는 onCreateViewHolder와 달리, 스와이프할 때마다 데이터를 바인딩하기 위해 호출될 것.
     * onBindViewHoler가 position값을 받고 있기 때문에, RecyclerView에서의 클릭 이벤트를 처리할 수 있음. --> 어떤 앨범을 눌렀는 지에 따라 대응할 수 있으니까.
     * @param position recyclerView에서의 인덱스. albumList에 데이터를 순차적으로 바인딩하기 위해 필요.
     */
    override fun onBindViewHolder(holder: AlbumRVAdapter.ViewHolder, position: Int) {
        // albumList에서의 album 데이터에 대해 순차적으로 bind를 수행.
        // 즉, 받아온 album 데이터를 item 객체에 넣어줌.
        holder.bind(albumList[position])

        // ViewHolder의 item이 클릭 되었을 때 이벤트 처리를 위해서는
        // 클릭 리스너의 역할을 하는 MyItemClickListener 인터페이스의 함수인 onItemClick 함수를 호출하면 됨
        holder.itemView.setOnClickListener{
            mItemClickListener.onItemClick(albumList[position])
        }
    }

    /**
     * 데이터 세트 크기를 알려주는 함수. recyclerView가 마지막이 언제인지를 알려줌.
     */
    override fun getItemCount(): Int = albumList.size

    // AlbumRVAdapter의 내부에서 사용하던 ViewHolder의 클래스를 내부에서 생성
    /**
     * ViewHolder는 item view 객체가 삭제되지 않도록 붙잡아 두는 역할
     * @param binding ItemView를 잡기 위해 item 레이아웃 객체를 매개변수로 받음
     */
    inner class ViewHolder(val binding: ItemAlbumBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Album) {
            binding.itemAlbumTitleTv.text = album.title
            binding.itemAlbumSingerTv.text = album.singer
            binding.itemAlbumCoverImgIv.setImageResource(album.coverImg!!)
        }
    }
}