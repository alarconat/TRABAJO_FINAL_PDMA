package com.example.apptareas.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.apptareas.models.Compras
import com.example.apptareas.models.Examenes
import com.example.apptareas.models.TareasFacultad
import com.example.apptareas.models.TareasCasa
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

const val EXAMENES_COLLECTION_REF = "examenes"
const val TAREASFACULTAD_COLLECTION_REF = "tareasFacultad" // Nueva colección
const val TAREASCASA_COLLECTION_REF = "tareasCasa"
const val COMPRAS_COLLECTION_REF = "compras"
class StorageRepository() {
    fun user() = Firebase.auth.currentUser
    fun hasUser(): Boolean = Firebase.auth.currentUser != null

    fun getUserId(): String = Firebase.auth.currentUser?.uid.orEmpty()

    private val examenesRef: CollectionReference = Firebase
        .firestore.collection(EXAMENES_COLLECTION_REF)

    private val tareasFacultadRef: CollectionReference = Firebase
        .firestore.collection(TAREASFACULTAD_COLLECTION_REF)

    private val tareasCasaRef: CollectionReference = Firebase
        .firestore.collection(TAREASCASA_COLLECTION_REF)

    private val comprasRef:CollectionReference = Firebase
        .firestore.collection(COMPRAS_COLLECTION_REF)

    // ======================= MÉTODOS PARA EXÁMENES ==========================
    @RequiresApi(Build.VERSION_CODES.O)
    fun getUserExamenes(
        userId: String,
        todosRegistros: String = "N"
    ): Flow<Resources<List<Examenes>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration? = null

        try {
            val fechaDeHoy = LocalDate.now().format(DateTimeFormatter.ISO_DATE)

            // Construir la consulta base
            var query = examenesRef.whereEqualTo("userId", userId)

            // Si no se quieren "todos los registros", agregar el filtro de fecha
            if (todosRegistros != "S") {
                query = query.whereEqualTo("fecha", fechaDeHoy)
            }

            snapshotStateListener = query.addSnapshotListener { snapshot, e ->
                val response = if (snapshot != null) {
                    val examenes = snapshot.toObjects(Examenes::class.java)
                    Resources.Success(data = examenes)
                } else {
                    Resources.Error(throwable = e?.cause)
                }
                trySend(response)
            }

        } catch (e: Exception) {
            trySend(Resources.Error(e.cause))
            e.printStackTrace()
        }
        awaitClose { snapshotStateListener?.remove() }
    }

    fun getExamen(examenId: String, onError: (Throwable?) -> Unit, onSuccess: (Examenes?) -> Unit) {
        examenesRef
            .document(examenId)
            .get()
            .addOnSuccessListener { onSuccess.invoke(it.toObject(Examenes::class.java)) }
            .addOnFailureListener { result -> onError.invoke(result.cause) }
    }

    fun addExamen(
        userId: String,
        materia: String,
        description: String,
        fecha: String,
        dia: String,
        hora: String,
        timestamp: Timestamp,
        color: Int = 0,
        onComplete: (Boolean) -> Unit
    ) {
        val documentId = examenesRef.document().id
        val examen = Examenes(
            userId, materia, description, fecha, dia, hora, timestamp, colorIndex = color, documentId = documentId
        )
        examenesRef.document(documentId).set(examen).addOnCompleteListener { result ->
            onComplete.invoke(result.isSuccessful)
        }
    }

    fun updateExamen(
        materia: String,
        description: String,
        fecha: String,
        dia: String,
        hora: String,
        color: Int,
        examenId: String,
        onResult: (Boolean) -> Unit
    ) {
        val updateData = hashMapOf<String, Any>(
            "colorIndex" to color,
            "description" to description,
            "materia" to materia,
            "fecha" to fecha,
            "dia" to dia,
            "hora" to hora
        )

        examenesRef.document(examenId).update(updateData).addOnCompleteListener {
            onResult(it.isSuccessful)
        }
    }

    fun deleteExamen(examenId: String, onComplete: (Boolean) -> Unit) {
        examenesRef.document(examenId).delete().addOnCompleteListener {
            onComplete.invoke(it.isSuccessful)
        }
    }

    // ======================= MÉTODOS PARA TAREASFACULTAD ==========================
    @RequiresApi(Build.VERSION_CODES.O)
    fun getUserTareasFacultad(
        userId: String,
        todosRegistros: String = "N"
    ): Flow<Resources<List<TareasFacultad>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration? = null

        try {
            val fechaDeHoy = LocalDate.now().format(DateTimeFormatter.ISO_DATE)

            // Construir la consulta base
            var query = tareasFacultadRef.whereEqualTo("userId", userId)

            // Si no se quieren "todos los registros", agregar el filtro de fecha
            if (todosRegistros != "S") {
                query = query.whereEqualTo("fecha", fechaDeHoy)
            }

            snapshotStateListener = query.addSnapshotListener { snapshot, e ->
                val response = if (snapshot != null) {
                    val tareas = snapshot.toObjects(TareasFacultad::class.java)
                    Resources.Success(data = tareas)
                } else {
                    Resources.Error(throwable = e?.cause)
                }
                trySend(response)
            }

        } catch (e: Exception) {
            trySend(Resources.Error(e.cause))
            e.printStackTrace()
        }
        awaitClose { snapshotStateListener?.remove() }
    }

    fun getTareaFacultad(
        tareaId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (TareasFacultad?) -> Unit
    ) {
        tareasFacultadRef.document(tareaId).get()
            .addOnSuccessListener { onSuccess.invoke(it.toObject(TareasFacultad::class.java)) }
            .addOnFailureListener { result -> onError.invoke(result.cause) }
    }

    fun addTareaFacultad(
        userId: String,
        materia: String,
        description: String,
        fecha: String,
        dia: String,
        hora: String,
        timestamp: Timestamp,
        color: Int = 0,
        onComplete: (Boolean) -> Unit
    ) {
        val documentId = tareasFacultadRef.document().id
        val tarea = TareasFacultad(
            userId, materia, description, fecha, dia, hora, timestamp, colorIndex = color, documentId = documentId
        )
        tareasFacultadRef.document(documentId).set(tarea).addOnCompleteListener { result ->
            onComplete.invoke(result.isSuccessful)
        }
    }

    fun updateTareaFacultad(
        materia: String,
        description: String,
        fecha: String,
        dia: String,
        hora: String,
        color: Int,
        tareaId: String,
        onResult: (Boolean) -> Unit
    ) {
        val updateData = hashMapOf<String, Any>(
            "colorIndex" to color,
            "description" to description,
            "materia" to materia,
            "fecha" to fecha,
            "dia" to dia,
            "hora" to hora
        )

        tareasFacultadRef.document(tareaId).update(updateData).addOnCompleteListener {
            onResult(it.isSuccessful)
        }
    }

    fun deleteTareaFacultad(tareaId: String, onComplete: (Boolean) -> Unit) {
        tareasFacultadRef.document(tareaId).delete().addOnCompleteListener {
            onComplete.invoke(it.isSuccessful)
        }
    }

    // ======================= MÉTODOS PARA TAREAS DE LA CASA ==========================
    @RequiresApi(Build.VERSION_CODES.O)
    fun getUserTareasCasa(
        userId: String,
        todosRegistros: String = "N"
    ): Flow<Resources<List<TareasCasa>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration? = null

        try {
            val fechaDeHoy = LocalDate.now().format(DateTimeFormatter.ISO_DATE)

            // Construir la consulta base
            var query = tareasCasaRef.whereEqualTo("userId", userId)

            // Si no se quieren "todos los registros", filtrar por fecha
            if (todosRegistros != "S") {
                query = query.whereEqualTo("fecha", fechaDeHoy)
            }

            snapshotStateListener = query.addSnapshotListener { snapshot, e ->
                val response = if (snapshot != null) {
                    val tareasCasa = snapshot.toObjects(TareasCasa::class.java)
                    Resources.Success(data = tareasCasa)
                } else {
                    Resources.Error(throwable = e?.cause)
                }
                trySend(response)
            }

        } catch (e: Exception) {
            trySend(Resources.Error(e.cause))
            e.printStackTrace()
        }
        awaitClose { snapshotStateListener?.remove() }
    }

    fun getTareasCasa(tareasCasaId: String, onError: (Throwable?) -> Unit, onSuccess: (TareasCasa?) -> Unit) {
        tareasCasaRef
            .document(tareasCasaId)
            .get()
            .addOnSuccessListener { onSuccess.invoke(it.toObject(TareasCasa::class.java)) }
            .addOnFailureListener { result -> onError.invoke(result.cause) }
    }

    fun addTareasCasa(
        userId: String,
        description: String,
        fecha: String,
        dia: String,
        hora: String,
        timestamp: Timestamp,
        color: Int = 0,
        onComplete: (Boolean) -> Unit
    ) {
        val documentId = tareasCasaRef.document().id
        val tareasCasa = TareasCasa(
            userId, description, fecha, dia, hora, timestamp, colorIndex = color, documentId = documentId
        )
        tareasCasaRef.document(documentId).set(tareasCasa).addOnCompleteListener { result ->
            onComplete.invoke(result.isSuccessful)
        }
    }

    fun updateTareasCasa(
        description: String,
        fecha: String,
        dia: String,
        hora: String,
        color: Int,
        tareasCasaId: String,
        onResult: (Boolean) -> Unit
    ) {
        val updateData = hashMapOf<String, Any>(
            "colorIndex" to color,
            "description" to description,
            "fecha" to fecha,
            "dia" to dia,
            "hora" to hora
        )

        tareasCasaRef.document(tareasCasaId).update(updateData).addOnCompleteListener {
            onResult(it.isSuccessful)
        }
    }

    fun deleteTareasCasa(tareasCasaId: String, onComplete: (Boolean) -> Unit) {
        tareasCasaRef.document(tareasCasaId).delete().addOnCompleteListener {
            onComplete.invoke(it.isSuccessful)
        }
    }

    // ======================= MÉTODOS PARA LISTA DE COMPRAS ==========================
    fun getUserCompras(
        userId: String
    ): Flow<Resources<List<Compras>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration? = null

        try {
            snapshotStateListener = comprasRef
                // Ordena por "producto"
                .orderBy("producto")
                // Filtra por "userId"
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        // Convierte los resultados a una lista de objetos Compras
                        val compras = snapshot.toObjects(Compras::class.java)
                        Resources.Success(data = compras)
                    } else {
                        Resources.Error(throwable = e?.cause)
                    }
                    trySend(response)
                }

        } catch (e: Exception) {
            trySend(Resources.Error(e?.cause))
            e.printStackTrace()
        }
        awaitClose {
            snapshotStateListener?.remove()
        }
    }


    fun getCompras(
        comprasId:String,
        onError:(Throwable?) -> Unit,
        onSuccess:(Compras?) -> Unit,
    ) {
        comprasRef
            .document(comprasId)
            .get()
            .addOnSuccessListener {
                onSuccess.invoke(it.toObject(Compras::class.java))
            }
            .addOnFailureListener { result ->
                onError.invoke(result.cause)
            }
    }

    fun addCompras(
        userId: String,
        producto: String,
        marca: String,
        cantidad: String,
        onComplete: (Boolean) -> Unit,
    ){
        val documentId = comprasRef.document().id
        val compras = Compras(
            userId,
            producto,
            marca,
            cantidad,
            documentId = documentId
        )
        comprasRef
            .document(documentId)
            .set(compras)
            .addOnCompleteListener{ result ->
                onComplete.invoke(result.isSuccessful)
            }
    }

    fun deleteCompras(comprasId: String, onComplete: (Boolean) -> Unit){
        comprasRef.document(comprasId)
            .delete()
            .addOnCompleteListener{
                onComplete.invoke(it.isSuccessful)
            }
    }

    fun updateCompras(
        producto: String,
        marca:String,
        cantidad:String,
        comprasId: String,
        onResult:(Boolean) -> Unit
    ){
        val updateData = hashMapOf<String,Any>(
            "producto" to producto,
            "marca" to marca,
            "cantidad" to cantidad,
        )

        comprasRef.document(comprasId)
            .update(updateData)
            .addOnCompleteListener{
                onResult(it.isSuccessful)
            }
    }

    fun signOut() = Firebase.auth.signOut()
}

sealed class Resources<T>(
    val data: T? = null,
    val throwable: Throwable? = null,
) {
    class Loading<T> : Resources<T>()
    class Success<T>(data: T?) : Resources<T>(data = data)
    class Error<T>(throwable: Throwable?) : Resources<T>(throwable = throwable)
}