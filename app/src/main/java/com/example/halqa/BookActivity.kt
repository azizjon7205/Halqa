package com.example.halqa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.halqa.databinding.ActivityBookBinding

class BookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() {
        when (intent.getStringExtra("language")) {
            getString(R.string.language_latin) -> {
                binding.pdfbook.fromAsset("Halqa.pdf").load()
            }
            getString(R.string.language_krill) -> {
                binding.pdfbook.fromAsset("ҲАЛҚА.pdf").load()
            }

        }
    }
}