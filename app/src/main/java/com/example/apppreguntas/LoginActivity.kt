package com.example.apppreguntas

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.apppreguntas.databinding.LoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.usuario.doOnTextChanged { text, start, before, count ->
            mostrarBotonUser(text)
        }

        binding.password.doOnTextChanged { text, start, before, count ->
            mostrarBotonPassword(text)
        }

        binding.boton.setOnClickListener {
            val passwordCifrada = cifrar(binding.password.text.toString(), "qwertyu")
            llamada(binding.usuario.text.toString(), passwordCifrada)
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun llamada(usuario: String, passwordCifrada: String) {

        val client = OkHttpClient()

        val request = Request.Builder()
        request.url("http://10.0.2.2:8082/anadirDb/${usuario}/${passwordCifrada}")


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
                        binding.cargando2.visibility = View.VISIBLE
                        delay(1000)
                        binding.cargando2.visibility = View.GONE
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("TOKEN", body)
                        startActivity(intent)
                    }
                }
            }
        })
    }

    fun validarUsuario(usuario: CharSequence?): Boolean {
        if (usuario != null) {
            if (usuario.length < 3) {
                return false
            }
        }
        usuario?.forEach {
            if (!(it.isLetter()))
                return false
        }
        return true
    }


    fun validarPassword(password: CharSequence?): Boolean {
        var contadorVocales = 0
        var contadorNumeros = 0
        if (password != null) {
            if (password.length != 8) {
                return false
            }
        }
        password?.forEach {
            if (it.isLetter()) {
                contadorVocales++
            } else if (it.isDigit())
                contadorNumeros++
        }
        if (contadorNumeros == 0 || contadorVocales == 0)
            return false

        return true
    }

    fun mostrarBotonUser(text: CharSequence?) {
        if (validarUsuario(text)) {
            if (validarPassword(binding.password.text.toString())) {
                binding.boton.visibility = View.VISIBLE
            } else binding.boton.visibility = View.GONE
        } else binding.boton.visibility = View.GONE
    }

    fun mostrarBotonPassword(text: CharSequence?) {
        if (validarUsuario(binding.usuario.text.toString())) {
            if (validarPassword(text)) {
                binding.boton.visibility = View.VISIBLE
            } else binding.boton.visibility = View.GONE
        } else binding.boton.visibility = View.GONE
    }

    private fun cifrar(textoEnString: String, llaveEnString: String): String {
        println("Voy a cifrar: $textoEnString")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, getKey(llaveEnString))
        val textCifrado = Base64.getEncoder()
            .encodeToString(cipher.doFinal(textoEnString.toByteArray(Charsets.UTF_8)))
        println("He obtenido $textCifrado")
        return textCifrado
    }

    private fun getKey(llaveEnString: String): SecretKeySpec {
        var llaveUtf8 = llaveEnString.toByteArray(Charsets.UTF_8)
        val sha = MessageDigest.getInstance("SHA-256")
        llaveUtf8 = sha.digest(llaveUtf8)
        llaveUtf8 = llaveUtf8.copyOf(16)
        return SecretKeySpec(llaveUtf8, "AES")
    }
}