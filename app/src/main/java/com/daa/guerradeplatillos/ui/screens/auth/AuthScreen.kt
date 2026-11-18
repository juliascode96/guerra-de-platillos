package com.daa.guerradeplatillos.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daa.guerradeplatillos.R
import com.daa.guerradeplatillos.ui.components.AppTextField
import com.daa.guerradeplatillos.ui.components.BorderCard
import com.daa.guerradeplatillos.ui.components.CircleIconButton
import com.daa.guerradeplatillos.ui.components.PrimaryButton
import com.daa.guerradeplatillos.ui.components.ScreenContainer
import com.daa.guerradeplatillos.ui.components.SelectableCard
import com.daa.guerradeplatillos.ui.theme.AppTheme
import com.daa.guerradeplatillos.util.SoundManager
import com.daa.guerradeplatillos.viewmodel.AuthViewModel

/**
 * Pantalla de Login y Registro conectada a Firebase Auth y Firestore.
 * Permite alternar entre "Iniciar sesión" y "Registrarse" en el mismo lugar.
 */
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    onSettingsClick: () -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel = viewModel(),
) {
    val colors = AppTheme.colors
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var esRegistro by rememberSaveable { mutableStateOf(false) }
    var username by rememberSaveable { mutableStateOf("") }
    var apodo by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    ScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header: volver + título de la app + ajustes
            Box(modifier = Modifier.fillMaxWidth()) {
                CircleIconButton(
                    icon = R.drawable.ic_arrow_back,
                    contentDescription = stringResource(R.string.btn_back_description),
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = stringResource(R.string.app_name),
                    color = colors.textHighlight,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
                CircleIconButton(
                    icon = R.drawable.ic_settings,
                    contentDescription = stringResource(R.string.btn_settings_description),
                    onClick = onSettingsClick,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            Text(
                text = if (esRegistro) "Creá tu cuenta" else "Iniciá sesión",
                color = colors.textPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 24.dp)
            )
            Text(
                text = if (esRegistro) {
                    "Elegí un usuario único y tu apodo para competir en el ranking."
                } else {
                    "Ingresá con tu usuario para recuperar tu perfil y puntajes."
                },
                color = colors.textSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Selector: Iniciar sesión / Registrarse
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SelectableCard(
                    text = "Iniciar sesión",
                    selected = !esRegistro,
                    onClick = {
                        esRegistro = false
                        viewModel.limpiarError()
                    },
                    modifier = Modifier.weight(1f)
                )
                SelectableCard(
                    text = "Registrarse",
                    selected = esRegistro,
                    onClick = {
                        esRegistro = true
                        viewModel.limpiarError()
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // Campo Usuario único
            FieldLabel("Usuario único", Modifier.padding(top = 20.dp))
            AppTextField(
                value = username,
                onValueChange = {
                    username = it.replace(" ", "")
                    viewModel.limpiarError()
                },
                placeholder = "Ej. chef_mateo99",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Ascii,
                    imeAction = ImeAction.Next,
                ),
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Solo letras, números o guión bajo (sin espacios).",
                color = colors.textSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Campo Apodo (solo en Registro; luego se sincroniza con el Avatar)
            if (esRegistro) {
                FieldLabel("Apodo (nombre del avatar)", Modifier.padding(top = 16.dp))
                AppTextField(
                    value = apodo,
                    onValueChange = {
                        apodo = it
                        viewModel.limpiarError()
                    },
                    placeholder = "Ej. El Rey del Brócoli",
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = "Es el nombre que verán todos en el ranking. Podrás editarlo en tu perfil.",
                    color = colors.textSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Campo Contraseña
            FieldLabel("Contraseña", Modifier.padding(top = 16.dp))
            AppTextField(
                value = password,
                onValueChange = {
                    password = it
                    viewModel.limpiarError()
                },
                placeholder = "Mínimo 6 caracteres",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(top = 8.dp)
            )

            // Mensaje de error si ocurre alguno
            val errorMsg = uiState.errorMessage
            if (errorMsg != null) {
                BorderCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = "⚠️ $errorMsg",
                        color = colors.error,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            PrimaryButton(
                text = when {
                    uiState.isLoading -> "Conectando..."
                    esRegistro -> "Registrarme y continuar  →"
                    else -> "Ingresar  →"
                },
                enabled = !uiState.isLoading,
                playSound = false,
                shape = RoundedCornerShape(28.dp),
                minHeight = 54.dp,
                onClick = {
                    if (esRegistro) {
                        viewModel.registrar(
                            username = username,
                            password = password,
                            apodo = apodo,
                            onSuccess = {
                                SoundManager.playBubbly()
                                Toast.makeText(context, "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show()
                                onAuthSuccess()
                            }
                        )
                    } else {
                        viewModel.iniciarSesion(
                            username = username,
                            password = password,
                            onSuccess = {
                                SoundManager.playBubbly()
                                Toast.makeText(context, "¡Bienvenido de nuevo!", Toast.LENGTH_SHORT).show()
                                onAuthSuccess()
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun FieldLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = AppTheme.colors.textPrimary,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}
