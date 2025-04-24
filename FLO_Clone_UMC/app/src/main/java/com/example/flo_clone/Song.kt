package com.example.flo_clone

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SongTable")
data class Song(
    var title: String? = "", // 곡명
    var singer: String? = "", // 가수명
    var second: Int = 0, // 곡이 얼마나 재생되었는지
    var playTime: Int = 0, // 총 재생 시간은 얼마인지
    var isPlaying: Boolean = false, // 현재 재생 중인지
    var music: String = "", // 어떤 음악을 재생시킬 것인지
    var coverImg: Int? = null,
    var isLike: Boolean = false,
    var albumTitle: String? = ""
//    val albumIdx: Int = 0
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
