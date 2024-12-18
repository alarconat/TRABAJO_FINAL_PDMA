package com.example.apptareas.TodosRegistros

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

class TodosRegistrosHomeViewModel(
    private val repository: StorageRepository = StorageRepository()
) : ViewModel() {
    var todosRegistrosHomeUiState by mutableStateOf(TodosRegistrosHomeUiState())

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
            todosRegistrosHomeUiState = todosRegistrosHomeUiState.copy(
                examenesList = Resources.Error(
                    throwable = Throwable(message = "Usuario no está logeado")
                )
            )
        }
    }

    private fun getUserExamenes(userId: String) = viewModelScope.launch {
        repository.getUserExamenes(userId = userId, todosRegistros = "S").collect {
            todosRegistrosHomeUiState = todosRegistrosHomeUiState.copy(examenesList = it)
        }
    }

    fun deleteExamen(examenId: String) = repository.deleteExamen(examenId) {
        todosRegistrosHomeUiState = todosRegistrosHomeUiState.copy(examenDeletedStatus = it)
    }

    // ======================= TAREAS FACULTAD ==========================
    fun loadTareasFacultad() {
        if (hasUser) {
            if (userId.isNotBlank()) {
                getUserTareasFacultad(userId)
            }
        } else {
            todosRegistrosHomeUiState = todosRegistrosHomeUiState.copy(
                tareasList = Resources.Error(
                    throwable = Throwable(message = "Usuario no está logeado")
                )
            )
        }
    }

    private fun getUserTareasFacultad(userId: String) = viewModelScope.launch {
        repository.getUserTareasFacultad(userId = userId, todosRegistros = "S").collect {
            todosRegistrosHomeUiState = todosRegistrosHomeUiState.copy(tareasList = it)
        }
    }

    fun deleteTareaFacultad(tareaId: String) = repository.deleteTareaFacultad(tareaId) {
        todosRegistrosHomeUiState = todosRegistrosHomeUiState.copy(tareaDeletedStatus = it)
    }

    // ======================= TAREAS DE LA CASA ==========================
    fun loadTareasCasa() {
        if (hasUser) {
            if (userId.isNotBlank()) {
                getUserTareasCasa(userId)
            }
        } else {
            todosRegistrosHomeUiState = todosRegistrosHomeUiState.copy(
                tareasCasaList = Resources.Error(
                    throwable = Throwable(message = "Usuario no está logeado")
                )
            )
        }
    }

    private fun getUserTareasCasa(userId: String) = viewModelScope.launch {
        repository.getUserTareasCasa(userId = userId, todosRegistros = "S").collect {
            todosRegistrosHomeUiState = todosRegistrosHomeUiState.copy(tareasCasaList = it)
        }
    }

    fun deleteTareasCasa(tareasCasaId: String) = repository.deleteTareasCasa(tareasCasaId) {
        todosRegistrosHomeUiState = todosRegistrosHomeUiState.copy(tareaDeletedStatus = it)
    }

    // ======================= CERRAR SESIÓN ==========================
    fun signOut() = repository.signOut()
}

data class TodosRegistrosHomeUiState(
    val examenesList: Resources<List<Examenes>> = Resources.Loading(), // Lista de exámenes
    val tareasList: Resources<List<TareasFacultad>> = Resources.Loading(), // Lista de TareasFacultad
    val tareasCasaList: Resources<List<TareasCasa>> = Resources.Loading(),
    val examenDeletedStatus: Boolean = false, // Estado de eliminación de exámenes
    val tareaDeletedStatus: Boolean = false, // Estado de eliminación de TareasFacultad
    val tareasCasaDeletedStatus: Boolean = false
)
