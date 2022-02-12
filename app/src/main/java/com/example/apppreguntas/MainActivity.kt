package com.example.apppreguntas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.apppreguntas.databinding.ActivityMainBinding
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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


    fun llamada(token : String) {

        val client = OkHttpClient()

        val request = Request.Builder()
        request.url("http://192.168.1.37:8082/Pregunta/${token}")


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

                        binding.tv1.text = pregunta.pregunta

                        binding.bt1.text = pregunta.respuesta1
                        binding.bt2.text = pregunta.respuesta2
                        binding.bt3.text = pregunta.respuesta3
                        binding.bt4.text = pregunta.respuesta4

                    }

                    binding.bt1.setOnClickListener {
                        if (binding.bt1.text == pregunta.respuestaCorrecta) {
                            Toast.makeText(this@MainActivity, "Está bien", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(this@MainActivity, "Está mal", Toast.LENGTH_SHORT)
                                .show()
                        }
                        llamada(token)
                    }

                    binding.bt2.setOnClickListener {
                        if (binding.bt2.text == pregunta.respuestaCorrecta) {
                            Toast.makeText(this@MainActivity, "Está bien", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(this@MainActivity, "Está mal", Toast.LENGTH_SHORT)
                                .show()
                        }
                        llamada(token)
                    }

                    binding.bt3.setOnClickListener {
                        if (binding.bt3.text == pregunta.respuestaCorrecta) {
                            Toast.makeText(this@MainActivity, "Está bien", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(this@MainActivity, "Está mal", Toast.LENGTH_SHORT)
                                .show()
                        }
                        llamada(token)
                    }

                    binding.bt4.setOnClickListener {
                        if (binding.bt4.text == pregunta.respuestaCorrecta) {
                            Toast.makeText(this@MainActivity, "Está bien", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(this@MainActivity, "Está mal", Toast.LENGTH_SHORT)
                                .show()
                        }
                        llamada(token)
                    }
                }
            }
        })
    }
}



