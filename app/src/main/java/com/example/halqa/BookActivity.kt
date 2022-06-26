package com.example.halqa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.halqa.databinding.ActivityBookBinding

class BookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews(){
        binding.pdfbook.fromAsset("Halqa.pdf").load()
    }
}