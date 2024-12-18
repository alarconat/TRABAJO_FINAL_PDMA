package com.example.apptareas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.apptareas.ComprasHome.ComprasHomeScreen
import com.example.apptareas.ComprasHome.ComprasHomeViewModel
import com.example.apptareas.TodosRegistros.TodosRegistrosHome
import com.example.apptareas.TodosRegistros.TodosRegistrosHomeViewModel
import com.example.apptareas.detail.Compras.ComprasScreen
import com.example.apptareas.detail.Compras.ComprasViewModel
import com.example.apptareas.detail.Examenes.ExamenScreen
import com.example.apptareas.detail.Examenes.ExamenViewModel
import com.example.apptareas.detail.TareasCasa.TareasCasaScreen
import com.example.apptareas.detail.TareasCasa.TareasCasaViewModel
import com.example.apptareas.detail.TareasFacultad.TareasFacultadScreen
import com.example.apptareas.detail.TareasFacultad.TareasFacultadViewModel
import com.example.apptareas.home.Home
import com.example.apptareas.home.HomeViewMode
import com.example.apptareas.login.LoginScreen
import com.example.apptareas.login.LoginViewModel
import com.example.apptareas.login.SignUpScreen

enum class LoginRoutes {
    Signup,
    SignIn
}

enum class HomeRoutes {
    Home,
    ExamenDetail,
    TareaFacultadDetail,
    TareasCasaDetail,
    ComprasHome,
    Compras,
    TodosRegistros
}

enum class NestedRoutes {
    Main,
    Login
}

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel,
    examenViewModel: ExamenViewModel,
    tareaFacultadViewModel: TareasFacultadViewModel,
    tareasCasaViewModel: TareasCasaViewModel,
    comprasHomeViewModel: ComprasHomeViewModel,
    todosRegistrosHomeViewModel: TodosRegistrosHomeViewModel,
    comprasViewModel: ComprasViewModel,
    homeViewMode: HomeViewMode
) {
    NavHost(
        navController = navController,
        startDestination = NestedRoutes.Login.name // Siempre inicia en el Login
    ) {
        authGraph(navController, loginViewModel)
        homeGraph(
            navController = navController,
            examenViewModel = examenViewModel,
            tareaFacultadViewModel = tareaFacultadViewModel,
            tareasCasaViewModel = tareasCasaViewModel,
            comprasHomeViewModel = comprasHomeViewModel,
            todosRegistrosHomeViewModel = todosRegistrosHomeViewModel,
            comprasViewModel = comprasViewModel,
            homeViewMode = homeViewMode
        )
    }
}

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel
) {
    navigation(
        startDestination = LoginRoutes.SignIn.name,
        route = NestedRoutes.Login.name
    ) {
        composable(route = LoginRoutes.SignIn.name) {
            LoginScreen(
                onNavToHomePage = {
                    navController.navigate(NestedRoutes.Main.name) {
                        launchSingleTop = true
                        popUpTo(route = LoginRoutes.SignIn.name) {
                            inclusive = true
                        }
                    }
                },
                loginViewModel = loginViewModel
            ) {
                navController.navigate(LoginRoutes.Signup.name)
            }
        }

        composable(route = LoginRoutes.Signup.name) {
            SignUpScreen(
                onNavToHomePage = {
                    navController.navigate(NestedRoutes.Main.name) {
                        popUpTo(LoginRoutes.Signup.name) {
                            inclusive = true
                        }
                    }
                },
                loginViewModel = loginViewModel
            ) {
                navController.navigate(LoginRoutes.SignIn.name)
            }
        }
    }
}

fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    examenViewModel: ExamenViewModel,
    tareaFacultadViewModel: TareasFacultadViewModel, // ViewModel para TareasFacultad
    tareasCasaViewModel: TareasCasaViewModel,
    comprasHomeViewModel: ComprasHomeViewModel,
    todosRegistrosHomeViewModel: TodosRegistrosHomeViewModel,
    comprasViewModel: ComprasViewModel,
    homeViewMode: HomeViewMode
) {
    navigation(
        startDestination = HomeRoutes.Home.name,
        route = NestedRoutes.Main.name
    ) {
        // Pantalla Home
        composable(HomeRoutes.Home.name) {
            Home(
                homeViewMode = homeViewMode,

                onExamenClick = { examenId ->
                    navController.navigate(
                        HomeRoutes.ExamenDetail.name + "?id=$examenId"
                    ) {
                        launchSingleTop = true
                    }
                },

                onTareaFacultadClick = { tareaId ->
                    navController.navigate(
                        HomeRoutes.TareaFacultadDetail.name + "?id=$tareaId"
                    ) {
                        launchSingleTop = true
                    }
                },

                onTareasCasaClick = { tareasCasaId ->
                    navController.navigate(
                        HomeRoutes.TareasCasaDetail.name + "?id=$tareasCasaId"
                    ) {
                        launchSingleTop = true
                    }
                },

                navToExamenPage = {
                    navController.navigate(HomeRoutes.ExamenDetail.name)
                },

                navToTareaFacultadPage = { // Navegación para agregar una nueva tarea
                    navController.navigate(HomeRoutes.TareaFacultadDetail.name)
                },

                navToComprasPage = {
                    navController.navigate(HomeRoutes.ComprasHome.name)
                },

                navToTodosRegistrosPage = {
                    navController.navigate(HomeRoutes.TodosRegistros.name)
                },

                navToTareasCasaPage = {
                    navController.navigate(HomeRoutes.TareasCasaDetail.name)
                }
            ) {
                navController.navigate(NestedRoutes.Login.name) {
                    launchSingleTop = true
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            }
        }

        // Pantalla Detalle Examen
        composable(
            route = HomeRoutes.ExamenDetail.name + "?id={id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { entry ->
            ExamenScreen(
                examenViewModel = examenViewModel,
                examenId = entry.arguments?.getString("id") ?: ""
            ) {
                navController.navigateUp()
            }
        }

        // Pantalla principal de Compras
        composable(route = HomeRoutes.ComprasHome.name) {
            ComprasHomeScreen(
                comprashomeViewModel = comprasHomeViewModel,

                onComprasClick = { comprasId ->
                    navController.navigate(HomeRoutes.Compras.name + "?id=$comprasId")
                },

                navToComprasHomePage = {
                    navController.navigate(HomeRoutes.ComprasHome.name)
                },

                navToExamenPage = {
                    navController.navigate(HomeRoutes.ExamenDetail.name)
                },
                navToComprasPage = {
                    navController.navigate(HomeRoutes.Compras.name)
                }
            ){
                navController.navigate(NestedRoutes.Login.name) {
                    launchSingleTop = true
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            }
        }

        // Pantalla principal de
        composable(route = HomeRoutes.TodosRegistros.name) {
            TodosRegistrosHome (
                todosRegistrosHomeViewModel = todosRegistrosHomeViewModel,

                onExamenClick = { examenId ->
                    navController.navigate(
                        HomeRoutes.ExamenDetail.name + "?id=$examenId"
                    ) {
                        launchSingleTop = true
                    }
                },

                onTareaFacultadClick = { tareaId ->
                    navController.navigate(
                        HomeRoutes.TareaFacultadDetail.name + "?id=$tareaId"
                    ) {
                        launchSingleTop = true
                    }
                },

                onTareasCasaClick = { tareasCasaId ->
                    navController.navigate(
                        HomeRoutes.TareasCasaDetail.name + "?id=$tareasCasaId"
                    ) {
                        launchSingleTop = true
                    }
                },

                navToExamenPage = {
                    navController.navigate(HomeRoutes.ExamenDetail.name)
                },

                navToTareaFacultadPage = { // Navegación para agregar una nueva tarea
                    navController.navigate(HomeRoutes.TareaFacultadDetail.name)
                },

                navToComprasPage = {
                    navController.navigate(HomeRoutes.ComprasHome.name)
                },

                navToTareasCasaPage = {
                    navController.navigate(HomeRoutes.TareasCasaDetail.name)
                }
            ){
                navController.navigate(NestedRoutes.Login.name) {
                    launchSingleTop = true
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            }
        }

        // Detalle de Compras
        composable(
            route = HomeRoutes.Compras.name + "?id={id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { entry ->
            ComprasScreen(
                comprasViewModel = comprasViewModel,
                comprasId = entry.arguments?.getString("id") as String
            ) {
                navController.navigateUp()
            }
        }

        // Pantalla Detalle TareasFacultad
        composable(
            route = HomeRoutes.TareaFacultadDetail.name + "?id={id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { entry ->
            TareasFacultadScreen(
                TareasFacultadViewModel = tareaFacultadViewModel,
                TareasFacultadId = entry.arguments?.getString("id") ?: ""
            ) {
                navController.navigateUp()
            }
        }

        // Pantalla Detalle Tareas de la Casa
        composable(
            route = HomeRoutes.TareasCasaDetail.name + "?id={id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { entry ->
            TareasCasaScreen(
                tareasCasaViewModel = tareasCasaViewModel,
                tareasCasaId = entry.arguments?.getString("id") ?: ""
            ) {
                navController.navigateUp()
            }
        }
    }
}


