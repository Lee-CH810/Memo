package com.example.flo_clone

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AlbumDao {
    @Insert
    fun insert(album: Album)

    @Update
    fun update(album: Album)

    @Delete
    fun delete(album: Album)

    /**
     * id에 맞는 Album 반환
     */
    @Query("SELECT * FROM AlbumTable WHERE id = :id")
    fun getAlbum(id: Int): Album

    /**
     * Album 전체 조회
     */
    @Query("SELECT * FROM AlbumTable")
    fun getAlbums(): List<Album>
    // ArrayList를 반환할 시, Not sure how to convert a Cursor to this method's return type 해당 오류 발생
    // --> List 반환!
}