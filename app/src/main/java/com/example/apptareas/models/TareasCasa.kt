package com.example.apptareas.models

import com.google.firebase.Timestamp

data class TareasCasa(
    val userId:String = "",
    val description:String = "",
    val fecha:String = "",
    val dia:String = "",
    val hora:String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val colorIndex: Int = 0,
    val documentId:String = "",
)
