package com.daa.guerradeplatillos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.daa.guerradeplatillos.GuerraDePlatillosApp
import com.daa.guerradeplatillos.ui.screens.game.GameOverScreen
import com.daa.guerradeplatillos.ui.screens.game.GameScreen
import com.daa.guerradeplatillos.ui.screens.home.HomeScreen
import com.daa.guerradeplatillos.ui.screens.menu.MainMenuScreen
import com.daa.guerradeplatillos.ui.screens.profile.EditProfileScreen
import com.daa.guerradeplatillos.ui.screens.profile.ProfileScreen
import com.daa.guerradeplatillos.ui.screens.ranking.RankingScreen
import com.daa.guerradeplatillos.ui.screens.settings.SettingsScreen
import com.daa.guerradeplatillos.util.SoundManager

/** Rutas de navegación de la app. */
object Routes {
    const val HOME = "home"
    const val CREATE_USER = "create_user"
    const val MAIN_MENU = "main_menu"
    const val GAME = "game"
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

    NavHost(navController = navController, startDestination = Routes.HOME) {

        // 1. Pantalla de portada
        composable(Routes.HOME) {
            HomeScreen(
                onPlayClick = {
                    // Si no hay perfil de usuario cargado, la segunda pantalla es crear usuario;
                    // si ya existe un perfil, pasa directo al menú principal.
                    if (avatar == null) {
                        navController.navigateSingleTop(Routes.CREATE_USER)
                    } else {
                        navController.navigateSingleTop(Routes.MAIN_MENU)
                    }
                },
                onSettingsClick = { navController.navigateSingleTop(Routes.SETTINGS) },
            )
        }

        // 2. Creación de nuevo usuario (segunda pantalla si no hay perfil cargado)
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

        // 3. Menú principal (enlaces a Jugar, Perfil y Ranking)
        composable(Routes.MAIN_MENU) {
            MainMenuScreen(
                onPlayClick = {
                    SoundManager.playStartGame()
                    navController.navigateSingleTop(Routes.GAME)
                },
                onProfileClick = { navController.navigateSingleTop(Routes.PROFILE) },
                onRankingClick = { navController.navigateSingleTop(Routes.RANKING) },
                onSettingsClick = { navController.navigateSingleTop(Routes.SETTINGS) },
                onBack = { navController.popBackStack() },
            )
        }

        // 4. Pantalla de juego (placeholder)
        composable(Routes.GAME) {
            GameScreen(
                onBack = { navController.popBackStack() },
                onFinishGame = {
                    SoundManager.playGameFinish()
                    navController.navigate(Routes.GAME_OVER) {
                        popUpTo(Routes.GAME) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onSettingsClick = { navController.navigateSingleTop(Routes.SETTINGS) },
            )
        }

        // 5. Pantalla de fin de juego / resultados (placeholder)
        composable(Routes.GAME_OVER) {
            GameOverScreen(
                onPlayAgain = {
                    SoundManager.playStartGame()
                    navController.navigate(Routes.GAME) {
                        popUpTo(Routes.GAME_OVER) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBackToMenu = {
                    navController.popBackStack(Routes.MAIN_MENU, inclusive = false)
                },
                onSettingsClick = { navController.navigateSingleTop(Routes.SETTINGS) },
            )
        }

        // 6. Pantalla de Ranking (placeholder)
        composable(Routes.RANKING) {
            RankingScreen(
                onBack = { navController.popBackStack() },
                onSettingsClick = { navController.navigateSingleTop(Routes.SETTINGS) },
            )
        }

        // 7. Configuración (opciones)
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onClose = { navController.popBackStack() },
                onProfileClick = { navController.navigateSingleTop(Routes.PROFILE) },
            )
        }

        // 8. Perfil de usuario
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

        // 9. Edición de perfil existente
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
