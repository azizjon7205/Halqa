package com.example.halqa

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.database.AppDatabase
import com.example.model.Halqa
import com.example.utils.SharedPref

class SplashActivity : AppCompatActivity() {
    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sharedPref = SharedPref(this)
        if (sharedPref.isOneCreate){
            Log.d("TAG", "onCreate: ")
            val appDatabase = AppDatabase.getInstance(this)

            val urls = resources.getStringArray(R.array.halqa).toList()

            urls.forEachIndexed { index, item ->
                val halqa = Halqa(bob = "${index + 1}-bob" , bookName = "Halqa", url = item, isDownload = false)
                appDatabase.halqaDao().createPost(halqa)
            }

            sharedPref.isOneCreate = false
        }


        handler= Handler()
        handler.postDelayed({
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        },3000)
    }
}