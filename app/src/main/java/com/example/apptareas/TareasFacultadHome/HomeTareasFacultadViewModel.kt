package com.example.apptareas.TareasFacultadHome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptareas.models.Examenes
import com.example.apptareas.models.TareasFacultad
import com.example.apptareas.models.TareasCasa
import com.example.apptareas.repository.Resources
import com.example.apptareas.repository.StorageRepository
import kotlinx.coroutines.launch

class HomeViewMode(
    private val repository: StorageRepository = StorageRepository()
) : ViewModel() {
    var homeUiState by mutableStateOf(HomeUiState())

    val user = repository.user()
    val hasUser: Boolean
        get() = repository.hasUser()
    private val userId: String
        get() = repository.getUserId()

    // ======================= EXÁMENES ==========================
    fun loadExamenes() {
        if (hasUser) {
            if (userId.isNotBlank()) {
                getUserExamenes(userId)
            }
        } else {
            homeUiState = homeUiState.copy(
                examenesList = Resources.Error(
                    throwable = Throwable(message = "Usuario no está logeado")
                )
            )
        }
    }

    private fun getUserExamenes(userId: String) = viewModelScope.launch {
        repository.getUserExamenes(userId).collect {
            homeUiState = homeUiState.copy(examenesList = it)
        }
    }

    fun deleteExamen(examenId: String) = repository.deleteExamen(examenId) {
        homeUiState = homeUiState.copy(examenDeletedStatus = it)
    }

    // ======================= TAREAS FACULTAD ==========================
    fun loadTareasFacultad() {
        if (hasUser) {
            if (userId.isNotBlank()) {
                if (homeUiState.tareasList !is Resources.Success) { // Evita recargar si ya tiene éxito
                    getUserTareasFacultad(userId)
                }
            }
        } else {
            homeUiState = homeUiState.copy(
                tareasList = Resources.Error(Throwable("Usuario no está logeado"))
            )
        }
    }

    private fun getUserTareasFacultad(userId: String) = viewModelScope.launch {
        repository.getUserTareasFacultad(userId).collect { result ->
            println("Estado de tareasList: $result") // LOG DEL ESTADO
            homeUiState = homeUiState.copy(tareasList = result)
        }
    }

    fun deleteTareaFacultad(tareaId: String) = repository.deleteTareaFacultad(tareaId) {
        homeUiState = homeUiState.copy(tareaDeletedStatus = it)
    }

    // ======================= TAREAS CASA ==========================
    fun loadTareasCasa() {
        if (hasUser) {
            if (userId.isNotBlank()) {
                getUserTareasCasa(userId)
            }
        } else {
            homeUiState = homeUiState.copy(
                tareasCasaList = Resources.Error(
                    throwable = Throwable(message = "Usuario no está logeado")
                )
            )
        }
    }

    private fun getUserTareasCasa(userId: String) = viewModelScope.launch {
        repository.getUserTareasCasa(userId).collect {
            homeUiState = homeUiState.copy(tareasCasaList = it)
        }
    }

    fun deleteTareasCasa(tareasCasaId: String) = repository.deleteTareasCasa(tareasCasaId) {
        homeUiState = homeUiState.copy(tareasCasaDeletedStatus = it)
    }
    fun signOut() = repository.signOut()
}

data class HomeUiState(
    val examenesList: Resources<List<Examenes>> = Resources.Loading(),
    val tareasList: Resources<List<TareasFacultad>> = Resources.Loading(), // Nueva lista para TareasFacultad
    val tareasCasaList: Resources<List<TareasCasa>> = Resources.Loading(),
    val examenDeletedStatus: Boolean = false,
    val tareaDeletedStatus: Boolean = false, // Nuevo estado para confirmar eliminación
    val tareasCasaDeletedStatus: Boolean = false // Nuevo estado para confirmar eliminación
)
