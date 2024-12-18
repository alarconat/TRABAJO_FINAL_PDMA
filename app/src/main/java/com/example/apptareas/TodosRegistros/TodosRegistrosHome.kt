package com.example.apptareas.TodosRegistros


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.apptareas.R
import com.example.apptareas.models.Examenes
import com.example.apptareas.models.TareasFacultad
import com.example.apptareas.models.TareasCasa
import com.example.apptareas.repository.Resources
import com.example.apptareas.ui.theme.ccasa
import com.example.apptareas.ui.theme.ccompras
import com.example.apptareas.ui.theme.cfacultad
import com.example.apptareas.ui.theme.cexamen

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable

fun TodosRegistrosHome(
    todosRegistrosHomeViewModel: TodosRegistrosHomeViewModel?,
    onExamenClick: (id: String) -> Unit,
    onTareaFacultadClick: (id: String) -> Unit,
    onTareasCasaClick: (id: String) -> Unit,
    navToExamenPage: () -> Unit,
    navToTareaFacultadPage: () -> Unit,
    navToTareasCasaPage: () -> Unit,
    navToComprasPage: () -> Unit,
    navToLoginPage: () -> Unit
) {
    val todosRegistrosHomeUiState = todosRegistrosHomeViewModel?.todosRegistrosHomeUiState ?: TodosRegistrosHomeUiState()

    var openDialog by remember { mutableStateOf(false) }
    var selectedExamen: Examenes? by remember { mutableStateOf(null) }
    var selectedTarea: TareasFacultad? by remember { mutableStateOf(null) }
    var selectedTareasCasa: TareasCasa? by remember { mutableStateOf(null) }

    var isMenuExpanded by remember { mutableStateOf(false) }
    val image = painterResource(R.drawable.logo_do_it_bg)
    LaunchedEffect(Unit) {
        todosRegistrosHomeViewModel?.loadExamenes()
        todosRegistrosHomeViewModel?.loadTareasFacultad()
        todosRegistrosHomeViewModel?.loadTareasCasa()
    }

    Scaffold(
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                AnimatedVisibility(visible = isMenuExpanded) {
                    Column(horizontalAlignment = Alignment.End) {
                        FloatingActionButton(
                            onClick = { navToExamenPage() },
                            containerColor = cexamen,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Row {
                                Icon(
                                    painter = painterResource(id = R.drawable.icon_book),
                                    contentDescription = "Exámenes"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Exámenes")
                            }
                        }

                        FloatingActionButton(
                            onClick = { navToTareaFacultadPage() },
                            containerColor = cfacultad,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Row {
                                Icon(
                                    painter = painterResource(id = R.drawable.icon_school),
                                    contentDescription = "Tareas Facultad"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Tareas Facultad")
                            }
                        }

                        FloatingActionButton(
                            onClick = { navToTareasCasaPage() },
                            containerColor = ccasa,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Row {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Tareas de la Casa"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Tareas de la Casa")
                            }
                        }
                        FloatingActionButton(
                            onClick = { navToComprasPage()},
                            containerColor = ccompras,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Compras"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Compras")
                            }
                        }
                    }
                }
                FloatingActionButton(onClick = { isMenuExpanded = !isMenuExpanded }) {
                    Icon(
                        imageVector = if (isMenuExpanded) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "Abrir menú"
                    )
                }
            }
        },
        topBar = {
            TopAppBar(
                actions = {
                    IconButton(onClick = {
                        todosRegistrosHomeViewModel?.signOut()
                        navToLoginPage()
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                },
                title = {
                    Image(
                        painter = image,
                        contentDescription = "Logo Do It",
                        modifier = Modifier.size(40.dp) // Ajusta el tamaño del logo
                    )
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Sección de Exámenes
            item {
                Text(
                    text = "Exámenes",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }
            when (val examenesList = todosRegistrosHomeUiState.examenesList) {
                is Resources.Success -> {
                    items(examenesList.data ?: emptyList()) { examen ->
                        ExamenItem(
                            examen = examen,
                            onClick = { onExamenClick(examen.documentId) },
                            onLongClick = {
                                openDialog = true
                                selectedExamen = examen
                            }
                        )
                    }
                }
                is Resources.Loading -> item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(align = Alignment.Center)
                    )
                }
                else -> item {
                    Text(
                        text = "Error al cargar exámenes",
                        color = Color.Red
                    )
                }
            }

            // Sección de Tareas Facultad
            item {
                Text(
                    text = "Tareas Facultad",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }
            when (val tareasList = todosRegistrosHomeUiState.tareasList) {
                is Resources.Success -> {
                    items(tareasList.data ?: emptyList()) { tarea ->
                        TareaFacultadItem(
                            tarea = tarea,
                            onClick = { onTareaFacultadClick(tarea.documentId) },
                            onLongClick = {
                                openDialog = true
                                selectedTarea = tarea
                            }
                        )
                    }
                }
                is Resources.Loading -> item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(align = Alignment.Center)
                    )
                }
                else -> item {
                    Text(
                        text = "Error al cargar tareas",
                        color = Color.Red
                    )
                }
            }

            // Sección de Tareas de la Casa
            item {
                Text(
                    text = "Tareas de la Casa",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }
            when (val tareasCasaList = todosRegistrosHomeUiState.tareasCasaList) {
                is Resources.Success -> {
                    items(tareasCasaList.data ?: emptyList()) { tareasCasa ->
                        TareasCasaItem(
                            tareaCasa = tareasCasa,
                            onClick = { onTareasCasaClick(tareasCasa.documentId) },
                            onLongClick = {
                                openDialog = true
                                selectedTareasCasa = tareasCasa
                            }
                        )
                    }
                }
                is Resources.Loading -> item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(align = Alignment.Center)
                    )
                }
                else -> item {
                    Text(
                        text = "Error al cargar tareas de la casa",
                        color = Color.Red
                    )
                }
            }

            // Diálogo para borrar ítems
            if (openDialog) {
                item {
                    AlertDialog(
                        onDismissRequest = { openDialog = false },
                        title = {
                            Text(
                                text = when {
                                    selectedExamen != null -> "¿Estas seguro de borrar?"
                                    selectedTarea != null -> "¿Estas seguro de borrar?"
                                    selectedTareasCasa != null -> "¿Estas seguro de borrar?"
                                    else -> "¿Seguro?"
                                }
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    selectedExamen?.documentId?.let {
                                        todosRegistrosHomeViewModel?.deleteExamen(it)
                                    }
                                    selectedTarea?.documentId?.let {
                                        todosRegistrosHomeViewModel?.deleteTareaFacultad(it)
                                    }
                                    selectedTareasCasa?.documentId?.let {
                                        todosRegistrosHomeViewModel?.deleteTareasCasa(it)
                                    }
                                    openDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text(text = "Borrar")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { openDialog = false }) {
                                Text(text = "Cancelar")
                            }
                        }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExamenItem(examen: Examenes, onClick: () -> Unit, onLongClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        colors = CardDefaults.cardColors(containerColor = cexamen)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Examen: ${examen.materia}", fontWeight = FontWeight.Bold)
            Text(text = "Descripción: ${examen.description}")
            Text(text = "Fecha: ${examen.fecha}")
            Text(text = "Hora: ${examen.hora}")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TareaFacultadItem(tarea: TareasFacultad, onClick: () -> Unit, onLongClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        colors = CardDefaults.cardColors(containerColor = cfacultad)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Tarea: ${tarea.materia}", fontWeight = FontWeight.Bold)
            Text(text = "Descripción: ${tarea.description}")
            Text(text = "Fecha Limite: ${tarea.fecha}")
            Text(text = "Hora Limite: ${tarea.hora}")
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TareasCasaItem(tareaCasa: TareasCasa, onClick: () -> Unit, onLongClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        colors = CardDefaults.cardColors(containerColor = ccasa)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Descripción: ${tareaCasa.description}", fontWeight = FontWeight.Bold)
            Text(text = "Fecha: ${tareaCasa.fecha}")
            Text(text = "Hora: ${tareaCasa.hora}")
        }
    }
}

