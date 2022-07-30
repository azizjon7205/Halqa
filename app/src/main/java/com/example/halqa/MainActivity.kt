package com.example.halqa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.halqa.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews(){
        /*binding.buttonLotincha.setOnClickListener {
            val intent = Intent(this, CategoryActivity::class.java)
            intent.putExtra("language", getString(R.string.language_latin))
            startActivity(intent)
        }
        binding.buttonKirilcha.setOnClickListener {
            val intent = Intent(this, CategoryActivity::class.java)
            intent.putExtra("language", getString(R.string.language_krill))
            startActivity(intent)
        }*/
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


    }

}