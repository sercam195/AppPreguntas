package com.example.apppreguntas

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.apppreguntas.databinding.ActivityMainBinding
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var token = intent.getStringExtra("TOKEN")
        llamada(token!!)
    }


    fun llamada(token: String) {

        val client = OkHttpClient()

        val request = Request.Builder()
        request.url("http://10.0.2.2:8082/Pregunta/${token}")


        val call = client.newCall(request.build())
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println(e.toString())
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(this@MainActivity, "Algo ha ido mal", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                println(response.toString())
                response.body?.let { responseBody ->
                    val body = responseBody.string()
                    println(body)
                    val gson = Gson()

                    val pregunta = gson.fromJson(body, Preguntas::class.java)

                    CoroutineScope(Dispatchers.Main).launch {
                        binding.cargando.visibility = View.VISIBLE
                        delay(1000)
                        binding.cargando.visibility = View.GONE
                        binding.tv1.text = pregunta.pregunta

                        binding.bt1.text = pregunta.respuesta1
                        binding.bt2.text = pregunta.respuesta2
                        binding.bt3.text = pregunta.respuesta3
                        binding.bt4.text = pregunta.respuesta4

                    }

                    binding.bt1.setOnClickListener {
                        (pulsarBoton(binding.bt1, token, pregunta.id))
                    }

                    binding.bt2.setOnClickListener {
                        (pulsarBoton(binding.bt2, token, pregunta.id))
                    }

                    binding.bt3.setOnClickListener {
                        (pulsarBoton(binding.bt3, token, pregunta.id))
                    }

                    binding.bt4.setOnClickListener {
                        (pulsarBoton(binding.bt4, token, pregunta.id))
                    }
                }
            }
        })
    }

    fun pulsarBoton(boton: android.widget.Button, token: String, id: Int) {

        val client = OkHttpClient()
        val respuesta = boton.text.toString()
        val request = Request.Builder()
        request.url("http://10.0.2.2:8082/Pregunta$id/${token}/${respuesta}")


        val call = client.newCall(request.build())
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println(e.toString())
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(this@MainActivity, "Algo ha ido mal", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                println(response.toString())
                response.body?.let { responseBody ->
                    val body = responseBody.string()
                    println(body)
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.cargando.visibility = View.VISIBLE
                        delay(1000)
                        binding.cargando.visibility = View.GONE
                        Toast.makeText(this@MainActivity, body, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
        llamada(token)
    }
}



