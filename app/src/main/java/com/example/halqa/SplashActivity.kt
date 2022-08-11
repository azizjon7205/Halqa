package com.example.halqa

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.database.AppDatabase
import com.example.model.BookData
import com.example.utils.Constants.HALQA
import com.example.utils.Constants.JANGCHI
import com.example.utils.SharedPref

class SplashActivity : AppCompatActivity() {
    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sharedPref = SharedPref(this)

        if (sharedPref.isOneCreate){
            val appDatabase = AppDatabase.getInstance(this)

            val halqaList = resources.getStringArray(R.array.halqa).toList()

            halqaList.forEachIndexed { index, item ->
                val halqa = BookData(bob = "${index + 1}-bob" , bookName = HALQA, url = item, isDownload = false)
                appDatabase.bookDao().createPost(halqa)
            }
            val jangchiList = resources.getStringArray(R.array.jangchi).toList()

            jangchiList.forEachIndexed { index, item ->
                val halqa = BookData(bob = "${index + 1}-bob" , bookName = JANGCHI, url = item, isDownload = false)
                appDatabase.bookDao().createPost(halqa)
            }

            sharedPref.isOneCreate = false
        }


        handler= Handler()
        handler.postDelayed({
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        },1000)
    }
}