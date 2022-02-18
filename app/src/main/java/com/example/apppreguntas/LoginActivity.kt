package com.example.apppreguntas

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.apppreguntas.databinding.LoginBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
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
        binding.usuario.text.clear()
        binding.password.text.clear()

        binding.usuario.doAfterTextChanged {
            mostrarBoton()
        }

        binding.password.doAfterTextChanged {
            mostrarBoton()
        }
    }

    private fun llamada(usuario: String, passwordCifrada: String) {

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
                        if (body == "Usuario ya registrado") {
                            Snackbar.make(
                                binding.root,
                                body,
                                BaseTransientBottomBar.LENGTH_SHORT
                            ).show()
                            binding.password.text.clear()
                        } else {
                            binding.cargando2.visibility = View.VISIBLE
                            delay(1000)
                            binding.cargando2.visibility = View.GONE
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.putExtra("TOKEN", body)
                            startActivity(intent)
                        }
                    }
                }
            }
        })
    }

    private fun validarUsuario(usuario: CharSequence?): Boolean {

        if (usuario != null) {
            if (usuario.length < 3) {
                return false
            }
            usuario.forEach {
                if (!(it.isLetter()))
                    return false
            }
            return true
        }
        return false
    }

    private fun validarPassword(password: CharSequence?): Boolean {
        var contadorVocales = 0
        var contadorNumeros = 0

        if (password != null) {
            if (password.length < 8) {
                return false
            }
            password.forEach {
                if (it.isLetter()) {
                    contadorVocales++
                } else if (it.isDigit())
                    contadorNumeros++
            }
            if (contadorNumeros == 0 || contadorVocales == 0)
                return false
            return true
        }
        return false
    }


    private fun mostrarBoton() {
        if (validarUsuario(binding.usuario.text.toString())) {
            if (validarPassword(binding.password.text.toString())) {
                botonEnable()
            } else {
                botonDisable()
            }
        } else {
            botonDisable()
        }
    }

    @SuppressLint("GetInstance")
    private fun cifrar(textoEnString: String, llaveEnString: String): Any? {
        println("Voy a cifrar: $textoEnString")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, getKey(llaveEnString))
        val textCifrado: String?
        if (Build.VERSION.SDK_INT >= 26) {
            textCifrado = Base64.getUrlEncoder()
                .encodeToString(cipher.doFinal(textoEnString.toByteArray(Charsets.UTF_8)))
        } else {
            textCifrado = android.util.Base64.encodeToString(
                cipher.doFinal(textoEnString.toByteArray(Charsets.UTF_8)),
                android.util.Base64.URL_SAFE
            )
        }
        println("He obtenido $textCifrado")
        return textCifrado
    }

    private fun botonDisable() {
        binding.boton.setBackgroundColor(ContextCompat.getColor(this, R.color.gris))
        binding.boton.setOnClickListener {
            Snackbar.make(
                binding.root,
                "Usuario o Contraseña mal introducido",
                BaseTransientBottomBar.LENGTH_SHORT
            ).show()
        }
    }

    private fun botonEnable() {
        binding.boton.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500))
        binding.boton.setOnClickListener {
            val sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE)
            if (sharedPreferences.getString("TAG", "No había nada").equals("No había nada")) {
                with(getSharedPreferences("Prefs", Context.MODE_PRIVATE).edit()) {
                    putString("TAG", generarLlave())
                    commit()
                }
            }
            val key = sharedPreferences.getString("TAG", "No había nada").equals("No había nada")
                .toString()

            println("LLAVE $key")
            val passwordCifrada = cifrar(binding.password.text.toString(), key)
            llamada(binding.usuario.text.toString(), passwordCifrada as String)
        }
    }

    private fun getKey(llaveEnString: String): SecretKeySpec {
        var llaveUtf8 = llaveEnString.toByteArray(Charsets.UTF_8)
        val sha = MessageDigest.getInstance("SHA-1")
        llaveUtf8 = sha.digest(llaveUtf8)
        llaveUtf8 = llaveUtf8.copyOf(16)
        return SecretKeySpec(llaveUtf8, "AES")
    }

    private fun generarLlave(): String {
        var palabra = ""
        repeat(8) {
            val abcd = 'a'..'z'
            val random1 = abcd.random()
            palabra += random1
        }
        return palabra
    }
}
