package com.example.flo_clone

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Song 데이터에 대한 Database
 */
// entities: 어떤 Table을 활용해서 DB를 구축할지 / version: DB의 버전. 수정 사항이 있다면 버전을 올려주어야 오류 발생하지 않음
@Database(entities = [Song::class, Album::class], version = 2)
abstract class MusicDatabase: RoomDatabase() {
    abstract fun SongDao() : SongDao
    abstract fun AlbumDao() : AlbumDao

    // 싱글톤 패턴
    companion object {
        private var instance: MusicDatabase? = null

        @Synchronized
        fun getInstance(context: Context): MusicDatabase? {
            if (instance == null) {
                synchronized(MusicDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MusicDatabase::class.java,
                        "music-database" // 다른 데이터 베이스랑 이름 겹치면 꼬임
                    ).allowMainThreadQueries().build()
                } // 원래는 데이터베이스에 연결해주는 작업을 워커 스레드로 작성해야함. 그렇지 않다면 오류 발생함. 메인스레드에도 작업이 있기 때문에
                // allowMainThreadQueries를 통해서 메인 스레드에 해당하는 작업을 넘겨줌. 효율적인 앱을 위해서는 이렇게 하면 안 됨. 편의상 사용함.
            }
            return instance
        }
    }
}