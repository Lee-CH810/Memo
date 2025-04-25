
package com.example.flo_clone

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SongDao {

    @Insert
    fun insert(song: Song)

    @Update
    fun update(song: Song)

    @Delete
    fun delete(song: Song)

    /**
     * 전체 조회
     */
    @Query("SELECT * FROM SongTable")
    fun getSongs(): List<Song>

    /**
     * 상세 조회
     */
    @Query("SELECT * FROM SongTable WHERE id = :id")
    fun getSong(id: Int): Song
    // :id --> 메서드가 넘겨받은 매개변수
    // id --> PK

    @Query("SELECT id FROM SongTable WHERE albumTitle = :title")
    fun getSongIdToAlbum(title: String): Int
}
