package com.example.apppreguntas

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.apppreguntas.databinding.PantallafinalBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

class FinalActivity : AppCompatActivity() {
    private lateinit var binding: PantallafinalBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = PantallafinalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val token = intent.getStringExtra("TOKEN")
        if (token != null) {
            contadorIntentos(token)
        }

        binding.siguiente.setOnClickListener {
            val intent = Intent(this@FinalActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    fun contadorIntentos(token: String) {

        val client = OkHttpClient()
        val request = Request.Builder()
        request.url("http://10.0.2.2:8082/mostrarPorcentaje/${token}")

        val call = client.newCall(request.build())
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println(e.toString())
                CoroutineScope(Dispatchers.Main).launch {
                    Snackbar.make(
                        binding.root,
                        "Algo ha ido mal",
                        BaseTransientBottomBar.LENGTH_SHORT
                    ).show()
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call, response: Response) {
                println(response.toString())
                response.body?.let { responseBody ->
                    val body = responseBody.string()
                    println(body)
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.tvFinal.text = "Porcentaje de $body% de aciertos"
                    }
                }
            }
        })
    }
}