package com.example.halqa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.halqa.databinding.ActivityCategoryBinding

class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews(){
        when(intent.getStringExtra("language")){
            getString(R.string.language_latin) -> {
                binding.ivBook1.setImageResource(R.drawable.im_halqa_pdf_latin)
                binding.ivBook2.setImageResource(R.drawable.im_jangchi_pdf_latin)
                binding.ivBook3.setImageResource(R.drawable.im_halqa_audio_1_latin)
                binding.ivBook4.setImageResource(R.drawable.im_jangchi_audio_1_latin)
                binding.ivBook5.setImageResource(R.drawable.im_halqa_audio_1_latin)
                binding.ivBook6.setImageResource(R.drawable.im_jangchi_audio_2_latin)

                binding.mcv1.setOnClickListener {
                    val intent = Intent(this, BookActivity::class.java)
                    intent.putExtra("language", getString(R.string.language_latin))
                    startActivity(intent)
                }
            }
            getString(R.string.language_krill) -> {
                binding.ivBook1.setImageResource(R.drawable.im_halqa_pdf_krill)
                binding.ivBook2.setImageResource(R.drawable.im_jangchi_pdf_krill)
                binding.ivBook3.setImageResource(R.drawable.im_halqa_audio_1_latin)
                binding.ivBook4.setImageResource(R.drawable.im_jangchi_audio_1_latin)
                binding.ivBook5.setImageResource(R.drawable.im_halqa_audio_1_latin)
                binding.ivBook6.setImageResource(R.drawable.im_jangchi_audio_2_latin)

                binding.mcv1.setOnClickListener {
                    val intent = Intent(this, BookActivity::class.java)
                    intent.putExtra("language", getString(R.string.language_krill))
                    startActivity(intent)
                }
            }
        }


    }
}