package com.example.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.model.Halqa

@Dao
interface HalqaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun createPost(halqa: Halqa)

    @Query("SELECT * FROM halqabook WHERE bookName = :bookName")
    fun getPosts(bookName: String): LiveData<List<Halqa>>

    @Query("UPDATE halqabook SET isDownload=:isDownload WHERE id=:id")
    fun updatePost(isDownload: Boolean, id: Int): Int

    @Query("SELECT * FROM halqabook WHERE id=:id")
    fun getPost(id: Int): LiveData<Halqa>

}