package com.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.Halqa

@Database(entities = [Halqa::class], version = 4)
abstract class AppDatabase: RoomDatabase(){
    abstract fun halqaDao(): HalqaDao

    companion object{
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase{
            if (instance == null){
                instance = Room.databaseBuilder(context, AppDatabase::class.java, "halqa.db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return instance as AppDatabase
        }
    }
}