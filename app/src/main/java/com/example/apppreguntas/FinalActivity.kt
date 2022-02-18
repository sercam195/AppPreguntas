package com.example.apppreguntas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.apppreguntas.databinding.PantallafinalBinding

class FinalActivity : AppCompatActivity() {
    private lateinit var binding: PantallafinalBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = PantallafinalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val correctas = intent.getStringExtra("CORRECTAS")
        binding.tvFinal.text = "Porcentaje de ${correctas}0%"

        binding.siguiente.setOnClickListener {
            val intent = Intent(this@FinalActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}