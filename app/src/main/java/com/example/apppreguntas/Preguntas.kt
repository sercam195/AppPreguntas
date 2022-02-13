package com.example.apppreguntas


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Preguntas(
    var pregunta: String,
    var respuesta1: String,
    var respuesta2: String,
    var respuesta3: String,
    var respuesta4: String,
    var respuestaCorrecta: String,
    var id: Int
) : Parcelable
