package com.example.memo_senior

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.memo_senior.databinding.ActivityCheckBinding

class CheckActivity : AppCompatActivity() {

    lateinit var binding: ActivityCheckBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.checkContentTv.text = intent.getStringExtra("inputText")

    }
}