package com.daa.guerradeplatillos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.daa.guerradeplatillos.GuerraDePlatillosApp
import com.daa.guerradeplatillos.ui.screens.auth.AuthScreen
import com.daa.guerradeplatillos.ui.screens.game.GameOverScreen
import com.daa.guerradeplatillos.ui.screens.game.GuerraPlatillosScreen
import com.daa.guerradeplatillos.ui.screens.game.InicioGuerraPlatillosScreen
import com.daa.guerradeplatillos.ui.screens.game.InicioLunchTimeScreen
import com.daa.guerradeplatillos.ui.screens.game.LunchTimeScreen
import com.daa.guerradeplatillos.ui.screens.game.OpcionLunchTimeScreen
import com.daa.guerradeplatillos.ui.screens.home.HomeScreen
import com.daa.guerradeplatillos.ui.screens.menu.MainMenuScreen
import com.daa.guerradeplatillos.ui.screens.profile.EditProfileScreen
import com.daa.guerradeplatillos.ui.screens.profile.ProfileScreen
import com.daa.guerradeplatillos.ui.screens.ranking.RankingScreen
import com.daa.guerradeplatillos.ui.screens.settings.SettingsScreen
import com.daa.guerradeplatillos.util.SoundManager

/** Rutas de navegaciÃ³n de la app. */
object Routes {
    const val HOME = "home"
    const val AUTH = "auth"
    const val CREATE_USER = "create_user"
    const val MAIN_MENU = "main_menu"

    // Rutas de Lunch Time
    const val INICIO_LUNCH_TIME = "inicio_lunch_time"
    const val OPCION_LUNCH_TIME = "opcion_lunch_time"
    const val LUNCH_TIME_GAME = "lunch_time_game"

    // Rutas de Guerra de Platillos
    const val INICIO_GUERRA_PLATILLOS = "inicio_guerra_platillos"
    const val GAME = "game" // Juego principal de Guerra de Platillos

    const val GAME_OVER = "game_over"
    const val RANKING = "ranking"
    const val SETTINGS = "settings"
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    val app = LocalContext.current.applicationContext as GuerraDePlatillosApp
    val avatar by app.userRepository.avatar.collectAsStateWithLifecycle()
    val currentUserUid by app.authRepository.currentUserUid.collectAsStateWithLifecycle()

    NavHost(navController = navController, startDestination = Routes.HOME) {

        // 1. Pantalla de portada
        composable(Routes.HOME) {
            HomeScreen(
                onPlayClick = {
                    when {
                        currentUserUid == null -> {
                            navController.navigateSingleTop(Routes.AUTH)
                        }
                        avatar == null -> {
                            navController.navigateSingleTop(Routes.CREATE_USER)
                        }
                        else -> {
                            navController.navigateSingleTop(Routes.MAIN_MENU)
                        }
                    }
                },
                onSettingsClick = { navController.navigateSingleTop(Routes.SETTINGS) },
            )
        }

        // 1b. Pantalla de Login / Registro (Firebase Auth + Firestore)
        composable(Routes.AUTH) {
            AuthScreen(
                onAuthSuccess = {
                    val destino = if (app.userRepository.avatar.value == null) {
                        Routes.CREATE_USER
                    } else {
                        Routes.MAIN_MENU
                    }
                    navController.navigate(destino) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onSettingsClick = { navController.navigateSingleTop(Routes.SETTINGS) },
                onBack = { navController.popBackStack() },
            )
        }

        // 2. CreaciÃ³n de nuevo usuario / avatar
        composable(Routes.CREATE_USER) {
            EditProfileScreen(
                isEditMode = false,
                onSaved = {
                    navController.navigate(Routes.MAIN_MENU) {
                        popUpTo(Routes.CREATE_USER) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onSettingsClick = { navController.navigateSingleTop(Routes.SETTINGS) },
                onBack = { navController.popBackStack() },
            )
        }

        // 3. MenÃº principal
        composable(Routes.MAIN_MENU) {
            MainMenuScreen(
                onPlayGame1Click = {
                    // Minijuego 1: Lunch Time
                    navController.navigateSingleTop(Routes.INICIO_LUNCH_TIME)
                },
                onPlayGame2Click = {
                    // Minijuego 2: Guerra de Platillos (Pantalla de inicio / presentaciÃ³n)
                    navController.navigateSingleTop(Routes.INICIO_GUERRA_PLATILLOS)
                },
                onProfileClick = { navController.navigateSingleTop(Routes.PROFILE) },
                onRankingClick = { navController.navigateSingleTop(Routes.RANKING) },
                onSettingsClick = { navController.navigateSingleTop(Routes.SETTINGS) },
                onBack = { navController.popBackStack() },
                onLogoutClick = {
                    app.authRepository.cerrarSesion()
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // 4. Pantalla de inicio / presentaciÃ³n de Lunch Time
        composable(Routes.INICIO_LUNCH_TIME) {
            InicioLunchTimeScreen(
                onStartGameClick = {
                    SoundManager.playStartGame()
                    navController.navigateSingleTop(Routes.OPCION_LUNCH_TIME)
                },
                onBack = { navController.popBackStack() }
            )
        }

        // 5. Pantalla de Opciones de Lunch Time (AutomÃ¡tico / Yo Elijo)
        composable(Routes.OPCION_LUNCH_TIME) {
            OpcionLunchTimeScreen(
                onConfirmClick = {
                    SoundManager.playStartGame()
                    navController.navigateSingleTop(Routes.LUNCH_TIME_GAME)
                },
                onBack = { navController.popBackStack() }
            )
        }

        // 6. Pantalla interactiva del juego Lunch Time (SelecciÃ³n de alimentos)
        composable(Routes.LUNCH_TIME_GAME) {
            LunchTimeScreen(
                onConfirmMenu = {
                    SoundManager.playGameFinish()
                    navController.navigate(Routes.GAME_OVER) {
                        popUpTo(Routes.MAIN_MENU) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // 7. Pantalla de inicio / presentaciÃ³n de Guerra de Platillos
        composable(Routes.INICIO_GUERRA_PLATILLOS) {
            InicioGuerraPlatillosScreen(
                onStartGameClick = {
                    SoundManager.playStartGame()
                    // Al presionar Â¡JUGAR! pasa al juego principal de Guerra de Platillos
                    navController.navigateSingleTop(Routes.GAME)
                },
                onBack = { navController.popBackStack() }
            )
        }

        // 8. Pantalla de juego principal (Guerra de Platillos)
        composable(Routes.GAME) {
            GuerraPlatillosScreen(
                onBack = { navController.popBackStack() },
                onFinishGame = { playerScore, rivalScore ->
                    SoundManager.playGameFinish()
                    navController.navigate("${Routes.GAME_OVER}?playerScore=$playerScore&rivalScore=$rivalScore") {
                        popUpTo(Routes.GAME) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onSettingsClick = { navController.navigateSingleTop(Routes.SETTINGS) },
            )
        }

        // 9. Pantalla de fin de juego / resultados
        composable(
            route = "${Routes.GAME_OVER}?playerScore={playerScore}&rivalScore={rivalScore}",
            arguments = listOf(
                navArgument("playerScore") {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument("rivalScore") {
                    type = NavType.IntType
                    defaultValue = -1
                },
            ),
        ) { backStackEntry ->
            val pScore = backStackEntry.arguments?.getInt("playerScore")?.takeIf { it >= 0 }
            val rScore = backStackEntry.arguments?.getInt("rivalScore")?.takeIf { it >= 0 }
            GameOverScreen(
                onPlayAgain = {
                    SoundManager.playStartGame()
                    navController.navigate(Routes.GAME) {
                        popUpTo("${Routes.GAME_OVER}?playerScore={playerScore}&rivalScore={rivalScore}") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBackToMenu = {
                    navController.popBackStack(Routes.MAIN_MENU, inclusive = false)
                },
                onSettingsClick = { navController.navigateSingleTop(Routes.SETTINGS) },
                playerScore = pScore,
                rivalScore = rScore,
            )
        }
        // 10. Pantalla de Ranking
        composable(Routes.RANKING) {
            RankingScreen(
                onBack = { navController.popBackStack() },
                onSettingsClick = { navController.navigateSingleTop(Routes.SETTINGS) },
            )
        }

        // 11. ConfiguraciÃ³n (opciones)
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onClose = { navController.popBackStack() },
                onProfileClick = { navController.navigateSingleTop(Routes.PROFILE) },
                onLogoutSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // 12. Perfil de usuario
        composable(Routes.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onSettingsClick = {
                    if (!navController.popBackStack(Routes.SETTINGS, inclusive = false)) {
                        navController.navigateSingleTop(Routes.SETTINGS)
                    }
                },
                onEditClick = { navController.navigateSingleTop(Routes.EDIT_PROFILE) },
            )
        }

        // 13. EdiciÃ³n de perfil existente
        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(
                isEditMode = true,
                onSaved = { navController.popBackStack() },
                onSettingsClick = {
                    if (!navController.popBackStack(Routes.SETTINGS, inclusive = false)) {
                        navController.navigateSingleTop(Routes.SETTINGS)
                    }
                },
                onBack = { navController.popBackStack() },
            )
        }
    }
}

private fun NavHostController.navigateSingleTop(route: String) =
    navigate(route) { launchSingleTop = true }
