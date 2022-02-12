package com.example.apppreguntas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.apppreguntas.databinding.LoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
        binding.usuario.doOnTextChanged { text, start, before, count ->
            if (validarUsuario()) {
                binding.password.doOnTextChanged { text, start, before, count ->
                    if (validarPassword()) {
                        binding.boton.visibility = View.VISIBLE
                    } else binding.boton.visibility = View.GONE
                }
            }
        }

         */

        binding.boton.setOnClickListener {
            var usuario = binding.usuario.text.toString()
            var password = binding.password.text.toString()
            llamada(usuario, password)
        }
    }

    fun llamada(usuario : String,password : String ) {

        val client = OkHttpClient()

        val request = Request.Builder()
        request.url("http://192.168.1.37:8082/anadirDb/${usuario}/${password}")


        val call = client.newCall(request.build())
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println(e.toString())
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(this@LoginActivity, "Algo ha ido mal", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                println(response.toString())
                response.body?.let { responseBody ->
                    val body = responseBody.string()
                    println(body)
                    CoroutineScope(Dispatchers.Main).launch {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("TOKEN", body)
                        startActivity(intent)
                    }
                }
            }
        })
    }
/*
    fun validarUsuario(): Boolean {
        val abcd = 'a'..'z'
        if (binding.usuario.text.length < 3){
            return false
        }
        binding.usuario.text.forEach {
            if (it !in abcd) {
                return false
            }
        }
        return true
    }
    fun validarPassword(): Boolean {
        val abcd = 'A'..'Z'
        val nums = mutableListOf<Char>('0','1','2','3','4','5','6','7','8','9')
        var contador = 0
        if (binding.password.text.length != 8) {
            return false
        }
        binding.usuario.text.toString().uppercase().forEach {
            if (it in abcd) {
                contador++
            }
            if (contador == binding.password.text.length)
                return false
        }
        nums.forEach {
            var u = it
            binding.usuario.text.toString().forEach {
                if (it == u)
                    return true
            }
        }
        return false
    }

 */
}
