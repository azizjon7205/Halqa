package com.example.database

import androidx.lifecycle.LiveData
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
    fun getPosts(bookName: String): LiveData<List<BookData>>

    @Query("UPDATE halqabook SET isDownload=:isDownload WHERE downloadID=:ID")
    fun updatePost(isDownload: Boolean, ID: Long): Int

    @Query("UPDATE halqabook SET downloadID=:ID WHERE id=:bookID")
    fun updateBookDownloadID(bookID: Int, ID: Long): Int

    @Query("SELECT * FROM halqabook WHERE id=:id")
    fun getPost(id: Int): LiveData<BookData>

}