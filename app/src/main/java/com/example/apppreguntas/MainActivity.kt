package com.example.apppreguntas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    var listaBotones = arrayListOf<android.widget.Button>()
    var intentos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listaBotones.add(binding.bt1)
        listaBotones.add(binding.bt2)
        listaBotones.add(binding.bt3)
        listaBotones.add(binding.bt4)

        binding.btPulsar.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_200))
        listaBotones.forEach { it.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500)) }
        var token = intent.getStringExtra("TOKEN")
        token?.let { llamada(token) }.run { println("Fue mal") }
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
                try {
                    response.body?.let { responseBody ->
                        val body = responseBody.string()
                        println(body)
                        val gson = Gson()
                        var respuesta = ""
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

                            binding.bc.setOnClickListener {
                                cambiarColor(binding.btPulsar)
                                binding.btPulsar.visibility = View.GONE
                            }
                        }

                        binding.bt1.setOnClickListener {
                            cambiarColor(binding.bt1)
                            respuesta = binding.bt1.text.toString()
                        }

                        binding.bt2.setOnClickListener {
                            cambiarColor(binding.bt2)
                            respuesta = binding.bt2.text.toString()
                        }

                        binding.bt3.setOnClickListener {
                            cambiarColor(binding.bt3)
                            respuesta = binding.bt3.text.toString()
                        }

                        binding.bt4.setOnClickListener {
                            cambiarColor(binding.bt4)
                            respuesta = binding.bt4.text.toString()
                        }
                        binding.btPulsar.setOnClickListener {
                            cambiarColor(binding.btPulsar)
                            (pulsarBoton(respuesta, token, pregunta.id))
                            binding.btPulsar.visibility = View.GONE
                        }
                    }
                } catch (e : Exception){
                    val intent = Intent(this@MainActivity, FinalActivity::class.java)
                    intent.putExtra("CORRECTAS", binding.tvContador.text.toString())
                    startActivity(intent)
                }
            }
        })
    }

    fun pulsarBoton(boton: String, token: String, id: Int) {

        val client = OkHttpClient()
        val request = Request.Builder()
        request.url("http://10.0.2.2:8082/Pregunta$id/${token}/$boton")

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
                        Toast.makeText(this@MainActivity, body, Toast.LENGTH_SHORT).show()
                        contadorAciertos(token)
                        intentos++
                        binding.tvIntentos.text = "Intentos: $intentos"
                        llamada(token)
                    }
                }
            }
        })
    }

    fun cambiarColor(boton: android.widget.Button) {
        listaBotones.forEachIndexed { index, button ->
            if (listaBotones[index] == boton) {
                boton.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_200))
            } else {
                button.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500))
            }
        }
        binding.btPulsar.visibility = View.VISIBLE
    }

    fun contadorAciertos(token: String) {

        val client = OkHttpClient()
        val request = Request.Builder()
        request.url("http://10.0.2.2:8082/MostrarCorrectas/${token}")

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
                        binding.tvContador.text = "Aciertos: $body"
                    }
                }
            }
        })
    }
}