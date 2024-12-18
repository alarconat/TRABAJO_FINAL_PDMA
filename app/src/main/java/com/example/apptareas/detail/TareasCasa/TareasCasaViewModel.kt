package com.example.apptareas.detail.TareasCasa

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.apptareas.models.TareasCasa
import com.example.apptareas.repository.StorageRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser

class TareasCasaViewModel(
    private val repository: StorageRepository = StorageRepository()
):ViewModel() {
    var detailUiState by mutableStateOf(DetailUiState())
        private set

    private val hasUser:Boolean
        get() = repository.hasUser()

    private val user: FirebaseUser?
        get() = repository.user()

    fun onColorChange(colorIndex: Int){
        detailUiState = detailUiState.copy(colorIndex = colorIndex)
    }

    fun onDescriptionChange(description: String){
        detailUiState = detailUiState.copy(description = description)
    }

    fun onFechaChange(fecha: String){
        detailUiState = detailUiState.copy(fecha = fecha)
    }

    fun onDiaChange(dia: String){
        detailUiState = detailUiState.copy(dia = dia)
    }

    fun onHoraChange(hora: String){
        detailUiState = detailUiState.copy(hora = hora)
    }

    fun addTareasCasa(){
        if (hasUser){
            repository.addTareasCasa(
                userId = user!!.uid,
                description = detailUiState.description,
                fecha = detailUiState.fecha,
                dia = detailUiState.dia,
                hora = detailUiState.hora,
                color = detailUiState.colorIndex,
                timestamp = Timestamp.now()
            ){
                detailUiState = detailUiState.copy(tareasCasaAddedStatus = it)
            }
        }
    }

    fun setEditFields(tareasCasa: TareasCasa){
        detailUiState = detailUiState.copy(
            colorIndex = tareasCasa.colorIndex,
            description = tareasCasa.description,
            fecha = tareasCasa.fecha,
            dia = tareasCasa.dia,
            hora = tareasCasa.hora,
        )
    }

    fun getTareasCasa(tareasCasaId:String){
        repository.getTareasCasa(
            tareasCasaId = tareasCasaId,
            onError = {},
        ){
            detailUiState = detailUiState.copy(selectedTareasCasa = it)
            detailUiState.selectedTareasCasa?.let { it1 -> setEditFields(it1) }
        }
    }

    fun updateTareasCasa(
        tareasCasaId: String
    ){
        repository.updateTareasCasa(
            description = detailUiState.description,
            tareasCasaId = tareasCasaId,
            fecha = detailUiState.fecha,
            dia = detailUiState.dia,
            hora = detailUiState.hora,
            color = detailUiState.colorIndex
        ){
            detailUiState = detailUiState.copy(updateTareasCasaStatus = it)
        }
    }

    fun resetTareasCasaAddedStatus(){
        detailUiState = detailUiState.copy(
            tareasCasaAddedStatus = false,
            updateTareasCasaStatus = false,
        )
    }

    fun resetState(){
        detailUiState = DetailUiState()
    }





}

data class DetailUiState(
    val colorIndex:Int= 0,
    val description:String="",
    val fecha:String="",
    val dia:String="",
    val hora:String="",
    val tareasCasaAddedStatus:Boolean = false,
    val updateTareasCasaStatus:Boolean = false,
    val selectedTareasCasa: TareasCasa? = null

)