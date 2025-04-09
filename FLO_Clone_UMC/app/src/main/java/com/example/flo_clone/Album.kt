package com.example.flo_clone

/**
 * item view 객체에 들어갈 data
 * @param title 제목
 * @param singer 가수
 * @param coverImg 커버 이미지
 * @param songs 앨범의 수록곡
 */
data class Album(
    var title: String? = "",
    var singer: String? = "",
    var coverImg: Int? = null,
    var songs: ArrayList<Song>? = null
)