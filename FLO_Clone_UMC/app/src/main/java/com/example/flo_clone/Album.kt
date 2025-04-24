package com.example.flo_clone

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * item view 객체에 들어갈 data
 * @param title 제목
 * @param singer 가수
 * @param coverImg 커버 이미지
 */
@Entity(tableName = "AlbumTable")
data class Album(
    var title: String? = "",
    var singer: String? = "",
    var coverImg: Int? = null
//    var songs: ArrayList<Song>? = null // 수록곡
){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}