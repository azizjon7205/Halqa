package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.model.BookData

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun createPost(halqa: BookData)

    @Query("SELECT * FROM halqabook WHERE bookName = :bookName")
    suspend fun getBookAudios(bookName: String): List<BookData>

    @Query("UPDATE halqabook SET isDownload=:isDownload WHERE downloadID=:ID")
    suspend fun updateDownload(isDownload: Boolean, ID: Long): Int

    @Query("UPDATE halqabook SET downloadID=:ID WHERE id=:bookID")
    suspend fun updateBookDownloadID(bookID: Int, ID: Long): Int

    @Query("SELECT * FROM halqabook WHERE id=:id")
    suspend fun getAudio(id: Int): BookData

    @Query("SELECT downloadID FROM halqabook WHERE id=:id")
    suspend fun getDownloadId(id: Int?): Int

}