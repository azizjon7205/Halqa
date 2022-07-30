package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.model.Halqa

@Dao
interface HalqaDao {

    @Insert
    fun createPost(halqa: Halqa)

    @Query("SELECT * FROM halqabook WHERE bookName = :bookName")
    fun getPosts(bookName: String): List<Halqa>

    @Query("UPDATE halqabook SET isDownload=:isDownload WHERE id=:id")
    fun updatePost(isDownload: Boolean, id: Int)

}