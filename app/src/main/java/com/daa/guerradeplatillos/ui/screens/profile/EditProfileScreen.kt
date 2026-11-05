package com.daa.guerradeplatillos.ui.screens.profile

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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daa.guerradeplatillos.R
import com.daa.guerradeplatillos.model.Avatar
import com.daa.guerradeplatillos.ui.components.AppTextField
import com.daa.guerradeplatillos.ui.components.CircleIconButton
import com.daa.guerradeplatillos.ui.components.PrimaryButton
import com.daa.guerradeplatillos.ui.components.ScreenContainer
import com.daa.guerradeplatillos.ui.components.SelectableCard
import com.daa.guerradeplatillos.ui.theme.AppTheme
import com.daa.guerradeplatillos.util.SoundManager
import com.daa.guerradeplatillos.viewmodel.ProfileViewModel

/**
 * Pantalla de creación y edición de los datos del usuario
 * (equivalente a Paso1AvatarActivity de Código Verde).
 *
 * @param isEditMode `false` cuando se muestra por primera vez para crear el usuario,
 *                   `true` cuando se entra desde el perfil para editar datos existentes.
 */
@Composable
fun EditProfileScreen(
    isEditMode: Boolean,
    onSaved: () -> Unit,
    onSettingsClick: () -> Unit,
    onBack: (() -> Unit)? = null,
    viewModel: ProfileViewModel = viewModel(),
) {
    val colors = AppTheme.colors
    val context = LocalContext.current
    val avatar by viewModel.avatar.collectAsStateWithLifecycle()
    val apodoRemoto by viewModel.apodoRemoto.collectAsStateWithLifecycle()

    var nombre by rememberSaveable { mutableStateOf("") }
    var edad by rememberSaveable { mutableStateOf("") }
    var esMasculino by rememberSaveable { mutableStateOf(true) }
    var precargado by rememberSaveable { mutableStateOf(false) }

    // Precarga de los datos guardados o del apodo traído de Firestore (una sola vez)
    LaunchedEffect(avatar, apodoRemoto) {
        val a = avatar
        if (!precargado) {
            if (a != null) {
                nombre = a.nombre
                if (a.edad > 0) edad = a.edad.toString()
                esMasculino = a.sexo != Avatar.SEXO_FEMENINO
                precargado = true
            } else if (apodoRemoto.isNotBlank()) {
                nombre = apodoRemoto
                precargado = true
            }
        }
    }

    ScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header: volver (si aplica) + nombre de la app + ruedita de ajustes
            Box(modifier = Modifier.fillMaxWidth()) {
                if (onBack != null) {
                    CircleIconButton(
                        icon = R.drawable.ic_arrow_back,
                        contentDescription = stringResource(R.string.btn_back_description),
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
                Text(
                    text = stringResource(R.string.app_name),
                    color = colors.textHighlight,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(if (onBack != null) Alignment.Center else Alignment.CenterStart)
                )
                CircleIconButton(
                    icon = R.drawable.ic_settings,
                    contentDescription = stringResource(R.string.btn_settings_description),
                    onClick = onSettingsClick,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            Text(
                text = stringResource(if (isEditMode) R.string.edit_title_edit else R.string.edit_title_create),
                color = colors.textPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 24.dp)
            )
            Text(
                text = stringResource(R.string.edit_subtitle),
                color = colors.textSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Vista previa del avatar (TODO: imagen elegida de AvatarCatalog)
            CircleIconButton(
                icon = R.drawable.ic_person,
                contentDescription = stringResource(R.string.desc_avatar_seleccionado),
                onClick = {},
                size = 110.dp,
                iconPadding = 20.dp,
                tint = colors.success,
                playSound = false,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 20.dp)
            )

            // Nombre
            FieldLabel(stringResource(R.string.label_nombre_avatar), Modifier.padding(top = 20.dp))
            AppTextField(
                value = nombre,
                onValueChange = { nombre = it },
                placeholder = stringResource(R.string.hint_nombre_avatar),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = stringResource(R.string.desc_nombre_avatar),
                color = colors.textSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Edad
            FieldLabel(stringResource(R.string.label_edad_usuario), Modifier.padding(top = 16.dp))
            AppTextField(
                value = edad,
                onValueChange = { nuevo -> if (nuevo.length <= 3 && nuevo.all(Char::isDigit)) edad = nuevo },
                placeholder = stringResource(R.string.hint_edad_avatar),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.padding(top = 8.dp)
            )

            // Género
            FieldLabel(stringResource(R.string.label_genero_usuario), Modifier.padding(top = 16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SelectableCard(
                    text = stringResource(R.string.option_hombre),
                    selected = esMasculino,
                    onClick = { esMasculino = true },
                    modifier = Modifier.weight(1f)
                )
                SelectableCard(
                    text = stringResource(R.string.option_mujer),
                    selected = !esMasculino,
                    onClick = { esMasculino = false },
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = stringResource(R.string.footer_cambiar_avatar),
                color = colors.textSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            )

            PrimaryButton(
                text = stringResource(if (isEditMode) R.string.btn_guardar_cambios else R.string.btn_continuar),
                playSound = false,
                shape = RoundedCornerShape(28.dp),
                minHeight = 54.dp,
                onClick = {
                    if (viewModel.guardarPerfil(nombre, edad, esMasculino)) {
                        SoundManager.playBubbly()
                        val msgRes = if (isEditMode) R.string.msg_datos_actualizados else R.string.msg_perfil_creado
                        Toast.makeText(context, msgRes, Toast.LENGTH_SHORT).show()
                        onSaved()
                    } else {
                        SoundManager.playError()
                        Toast.makeText(context, R.string.error_datos_avatar, Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
            Spacer(Modifier.height(8.dp))
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
