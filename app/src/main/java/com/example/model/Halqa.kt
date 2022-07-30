package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "HalqaBook")
data class Halqa(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var bob:String,
    var bookName: String,
    var url: String,
    var isDownload: Boolean = false
)