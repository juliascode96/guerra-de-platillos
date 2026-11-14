package com.daa.guerradeplatillos.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.window.Dialog
import com.daa.guerradeplatillos.R
import com.daa.guerradeplatillos.data.DiaSemanaEstado
import com.daa.guerradeplatillos.data.EstadisticasJugador
import com.daa.guerradeplatillos.data.NivelDia
import com.daa.guerradeplatillos.data.ResumenLunchTimeUsuario
import com.daa.guerradeplatillos.data.local.PartidaConPlatos
import com.daa.guerradeplatillos.model.Avatar
import com.daa.guerradeplatillos.model.AvatarCatalog
import com.daa.guerradeplatillos.model.DatoCurioso
import com.daa.guerradeplatillos.ui.components.BorderCard
import com.daa.guerradeplatillos.ui.components.CircleIconButton
import com.daa.guerradeplatillos.ui.components.PrimaryButton
import com.daa.guerradeplatillos.ui.components.ScreenContainer
import com.daa.guerradeplatillos.ui.theme.AppTheme
import com.daa.guerradeplatillos.ui.theme.GuerraDePlatillosTheme
import com.daa.guerradeplatillos.util.SoundManager
import com.daa.guerradeplatillos.viewmodel.ProfileViewModel

/**
 * Pantalla de Perfil de usuario (equivalente a DetallesUsuario de Código Verde).
 * Racha y partidas ganadas se calculan desde Firestore (`matches`); el dato curioso
 * se sortea de assets/datos_curiosos.json. "Días sin ultraprocesados" sigue siendo placeholder.
 */
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
    onEditClick: () -> Unit,
    onVerBarajaClick: () -> Unit = {},
    onRankingClick: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel(),
) {
    val context = LocalContext.current
    val avatar by viewModel.avatar.collectAsStateWithLifecycle()
    val estadisticas by viewModel.estadisticas.collectAsStateWithLifecycle()
    val datoCurioso by viewModel.datoCurioso.collectAsStateWithLifecycle()
    val resumenLunchTime by viewModel.resumenLunchTime.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.cargarDatosPerfil() }

    val proximamente = {
        Toast.makeText(context, "Próximamente", Toast.LENGTH_SHORT).show()
    }

    ProfileContent(
        avatar = avatar,
        estadisticas = estadisticas,
        resumenLunchTime = resumenLunchTime,
        datoCurioso = datoCurioso,
        onBack = onBack,
        onSettingsClick = onSettingsClick,
        onEditClick = onEditClick,
        onVerBarajaClick = onVerBarajaClick,
        onImprimirBarajaClick = proximamente,
        onRankingClick = onRankingClick,
    )
}

@Composable
private fun ProfileContent(
    avatar: Avatar?,
    estadisticas: EstadisticasJugador?,
    resumenLunchTime: ResumenLunchTimeUsuario?,
    datoCurioso: DatoCurioso?,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
    onEditClick: () -> Unit,
    onVerBarajaClick: () -> Unit,
    onImprimirBarajaClick: () -> Unit,
    onRankingClick: () -> Unit,
) {
    val colors = AppTheme.colors
    var diaSeleccionado by remember { mutableStateOf<DiaSemanaEstado?>(null) }

    ScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header: volver / título / ajustes
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                CircleIconButton(
                    icon = R.drawable.ic_arrow_back,
                    contentDescription = stringResource(R.string.btn_back_description),
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = stringResource(R.string.profile_title),
                    color = colors.textPrimary,
                    fontSize = 16.sp,
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

            // Foto y nombre
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    val avatarImg = avatar?.let { AvatarCatalog.byId(it.avatarId) }
                    if (avatarImg != null) {
                        Image(
                            painter = painterResource(avatarImg.drawable),
                            contentDescription = stringResource(R.string.desc_avatar_usuario),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .border(2.dp, colors.success, CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(colors.goldXp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_person),
                                contentDescription = stringResource(R.string.desc_avatar_usuario),
                                tint = colors.background,
                                modifier = Modifier.size(56.dp)
                            )
                        }
                    }
                    // Badge verificado
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(colors.success)
                            .border(2.dp, colors.background, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✓", color = colors.buttonPrimaryText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = avatar?.nombre ?: stringResource(R.string.profile_no_user),
                    color = colors.textPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    text = avatar?.let { stringResource(R.string.profile_user_info, it.edad, it.sexo) }
                        ?: stringResource(R.string.profile_no_user_info),
                    color = colors.textSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Accesos rápidos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ActionChip("🃏 Ver baraja", colors.badgeGreenBg, colors.success, onVerBarajaClick, Modifier.weight(1f))
                ActionChip("🖨️ Imprimir baraja", colors.badgeBlueBg, colors.textPrimary, onImprimirBarajaClick, Modifier.weight(1.2f))
                ActionChip("🏆 Ver ranking", colors.badgeOrangeBg, colors.textPrimary, onRankingClick, Modifier.weight(1f))
            }

            // Tarjetas de estadísticas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    value = resumenLunchTime?.diasSinUltraprocesados?.toString() ?: "-",
                    label = stringResource(R.string.profile_stat_1),
                    valueColor = colors.success,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = estadisticas?.rachaDias?.toString() ?: "-",
                    label = stringResource(R.string.profile_stat_2),
                    valueColor = colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = estadisticas?.partidasGanadas?.toString() ?: "-",
                    label = stringResource(R.string.profile_stat_3),
                    valueColor = colors.success,
                    modifier = Modifier.weight(1f)
                )
            }

            // Días de la semana
            Text(
                text = stringResource(R.string.profile_week_header),
                color = colors.textSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                val dias = resumenLunchTime?.diasSemana?.takeIf { it.size == 7 } ?: run {
                    val etiquetas = listOf("L", "M", "M", "J", "V", "S", "D")
                    val hoy = java.time.LocalDate.now()
                    etiquetas.mapIndexed { idx, label ->
                        DiaSemanaEstado(
                            fecha = hoy.plusDays(idx.toLong()),
                            etiqueta = label,
                            nivel = NivelDia.SIN_DATOS,
                            partidas = emptyList(),
                        )
                    }
                }
                dias.forEach { dia ->
                    val color = when (dia.nivel) {
                        NivelDia.ROJO -> colors.error
                        NivelDia.AMARILLO -> colors.warning
                        NivelDia.VERDE -> colors.success
                        NivelDia.SIN_DATOS -> colors.cardBorder
                    }
                    DayBox(
                        day = dia.etiqueta,
                        color = color,
                        onClick = {
                            SoundManager.playBubbly()
                            diaSeleccionado = dia
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ¿Sabías que...? (dato curioso al azar)
            datoCurioso?.let { dato ->
                InfoCard(
                    emoji = "💡",
                    title = stringResource(R.string.profile_fact_title),
                    titleColor = colors.success,
                    subtitle = dato.plato,
                    body = dato.dato,
                    modifier = Modifier.padding(top = 20.dp)
                )
            }

            // Botón Editar datos
            PrimaryButton(
                text = stringResource(R.string.btn_editar_datos),
                onClick = onEditClick,
                shape = RoundedCornerShape(28.dp),
                minHeight = 54.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            )
            Spacer(Modifier.height(8.dp))
        }
    }

    diaSeleccionado?.let { dia ->
        DiaDetalleDialog(
            dia = dia,
            onDismiss = { diaSeleccionado = null }
        )
    }
}

@Composable
private fun ActionChip(
    text: String,
    background: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .clickable {
                SoundManager.playBubbly()
                onClick()
            }
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 13.sp,
            maxLines = 2
        )
    }
}

@Composable
private fun StatCard(value: String, label: String, valueColor: Color, modifier: Modifier = Modifier) {
    BorderCard(
        modifier = modifier.height(115.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(value, color = valueColor, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(
                label,
                color = AppTheme.colors.textSecondary,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun DayBox(
    day: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color)
        )
        Text(
            text = day,
            color = AppTheme.colors.textSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun DiaDetalleDialog(
    dia: DiaSemanaEstado,
    onDismiss: () -> Unit,
) {
    val colors = AppTheme.colors
    val fechaFormateada = remember(dia.fecha) {
        val formatter = java.time.format.DateTimeFormatter.ofPattern("EEEE dd/MM", java.util.Locale("es", "AR"))
        dia.fecha.format(formatter).replaceFirstChar { it.uppercase() }
    }
    val horaFormatter = remember {
        java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
    }

    Dialog(onDismissRequest = onDismiss) {
        BorderCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = fechaFormateada,
                        color = colors.textHighlight,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    CircleIconButton(
                        icon = R.drawable.ic_close,
                        contentDescription = stringResource(R.string.btn_close_description),
                        onClick = onDismiss,
                    )
                }

                Spacer(Modifier.height(12.dp))

                if (dia.partidas.isEmpty()) {
                    Text(
                        text = "No jugaste partidas de Lunch Time este día.",
                        color = colors.textSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        dia.partidas.forEach { item: PartidaConPlatos ->
                            val p = item.partida
                            val hora = horaFormatter.format(java.util.Date(p.timestampMs))
                            val colorPuntaje = when {
                                p.puntajeTotal < 6 -> colors.error
                                p.puntajeTotal <= 12 -> colors.warning
                                else -> colors.success
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.cardUnselectedBg)
                                    .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "${p.comida} · $hora hs",
                                    color = colors.textSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "${p.puntajeTotal} / 18 pts",
                                    color = colorPuntaje,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "Salud: ${p.salud}/6",
                                    color = colors.textPrimary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "Saciedad: ${p.saciedad}/6",
                                    color = colors.textPrimary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "Vitalidad: ${p.vitalidad}/6",
                                    color = colors.textPrimary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(
    emoji: String,
    title: String,
    titleColor: Color,
    body: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    BorderCard(modifier = modifier.fillMaxWidth()) {
        Row {
            Text(emoji, fontSize = 20.sp, modifier = Modifier.padding(end = 12.dp))
            Column {
                Text(title, color = titleColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                subtitle?.let {
                    Text(
                        it,
                        color = AppTheme.colors.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Text(
                    body,
                    color = AppTheme.colors.textPrimary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProfilePreview() {
    GuerraDePlatillosTheme {
        ProfileContent(
            avatar = Avatar("Mateo", Avatar.SEXO_MASCULINO, 15),
            estadisticas = EstadisticasJugador(partidasGanadas = 7, rachaDias = 3),
            resumenLunchTime = ResumenLunchTimeUsuario(diasSinUltraprocesados = 2),
            datoCurioso = DatoCurioso("Yogur griego", "Aporta proteínas y calcio."),
            onBack = {}, onSettingsClick = {}, onEditClick = {},
            onVerBarajaClick = {}, onImprimirBarajaClick = {}, onRankingClick = {},
        )
    }
}
