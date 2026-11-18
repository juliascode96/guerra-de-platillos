package com.daa.guerradeplatillos.ui.screens.game

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daa.guerradeplatillos.R
import com.daa.guerradeplatillos.model.Comida
import com.daa.guerradeplatillos.ui.components.HowToSlide
import com.daa.guerradeplatillos.ui.theme.AppTheme
import kotlinx.coroutines.delay

// =====================================================================
//  LUNCH TIME! — Diapositivas del tutorial
// =====================================================================

val lunchTimeHowToSlides: List<HowToSlide> = listOf(
    HowToSlide(
        emoji = "🍽️",
        title = "¡Hora de comer!",
        body = "Tenés **20 segundos** para armar tu bandeja con hasta **3 alimentos**. " +
            "Elegí los más saludables para sumar puntos y ganar la partida.",
        tip = "Tocá un alimento para sumarlo a la bandeja y volvé a tocarlo para sacarlo. " +
            "Si terminás antes de tiempo, pulsá **CONFIRMAR MENÚ**. ¡Probalo acá arriba!",
        preview = { LunchTrayPreview() },
    ),
    HowToSlide(
        emoji = "⏰",
        title = "Elegí tu comida",
        body = "Antes de jugar elegís el modo: en **Automático** la app elige la comida según la hora " +
            "del día; en **Yo elijo** decidís vos si armar un desayuno, almuerzo, merienda o cena.",
        preview = { LunchModePreview() },
    ),
    HowToSlide(
        emoji = "❤️",
        title = "Cada alimento suma",
        body = "Cada alimento aporta hasta **2 puntos** en **Salud**, **Saciedad** y **Vitalidad**. " +
            "Con 3 alimentos podés llegar a un máximo de **18 puntos** (6 por categoría).",
        tip = "Los ultraprocesados casi no suman en salud ni vitalidad: ¡pensalo dos veces antes de elegirlos!",
        preview = { LunchPointsPreview() },
    ),
    HowToSlide(
        emoji = "🏆",
        title = "¿Ganaste o perdiste?",
        body = "Al terminar ves tus puntos por categoría. Si llegás al **60% del puntaje máximo** " +
            "(**11 de 18 puntos**) ¡ganás! Si no, perdés la partida.",
        tip = "En el resultado también aparece un **¿Sabías qué?** con un dato curioso sobre lo que elegiste. " +
            "Podés **volver a jugar** todas las veces que quieras.",
        preview = { LunchResultPreview() },
    ),
    HowToSlide(
        emoji = "🔥",
        title = "Días sin ultraprocesados",
        body = "Cada partida queda guardada en tu teléfono y se refleja en tu **Perfil**. " +
            "El contador de **días sin ultraprocesados** suma los días seguidos en los que " +
            "**todas** tus partidas sacaron al menos **4 de 6** en Salud, Saciedad y Vitalidad.",
        tip = "Si un día no llegás a 4 en alguna categoría, la racha vuelve a empezar. ¡Cuidala!",
        preview = { LunchStreakPreview() },
    ),
    HowToSlide(
        emoji = "🚦",
        title = "Tu semana en colores",
        body = "En el Perfil vas a ver un cuadrado por cada día de la semana (de lunes a domingo). " +
            "Su color depende de tu **peor partida** de ese día:\n" +
            "🟢 **13 a 18 puntos** · 🟡 **6 a 12 puntos** · 🔴 **0 a 5 puntos** · ⬜ sin jugar.",
        tip = "En tu Perfil, tocá un día para ver qué comiste en cada partida. " +
            "¡Tocá los cuadrados de arriba para probar!",
        preview = { LunchWeekPreview() },
    ),
)

// =====================================================================
//  GUERRA DE PLATILLOS — Diapositivas del tutorial
// =====================================================================

val guerraHowToSlides: List<HowToSlide> = listOf(
    HowToSlide(
        emoji = "⚔️",
        title = "¡El duelo está servido!",
        body = "Te enfrentás a un rival en un duelo de **3 rondas**. Cada uno recibe **3 cartas** de " +
            "platillos y en cada ronda juega una. Al final gana quien sume **más puntos**.",
        preview = { GuerraTablePreview() },
    ),
    HowToSlide(
        emoji = "🃏",
        title = "Conocé tus cartas",
        body = "Cada carta tiene tres valores: **E** Energía, **S** Saciedad y **V** Vitalidad. " +
            "El color te ayuda a leerlos rápido:\n🔴 **0 a 30** · 🟡 **31 a 65** · 🟢 **66 o más**.",
        tip = "Tocá una carta de tu mano para ver su detalle. La baraja completa está en " +
            "**Perfil → Ver baraja**, y podés escanear el QR de una carta física desde el menú principal.",
        preview = { GuerraCardPreview() },
    ),
    HowToSlide(
        emoji = "👆",
        title = "Jugá tu carta",
        body = "**Arrastrá hacia arriba** la carta que quieras jugar. El rival elige la suya en secreto " +
            "y las dos quedan **boca abajo** sobre la mesa.",
        preview = { GuerraDragPreview() },
    ),
    HowToSlide(
        emoji = "📳",
        title = "¡Agitá para revelar!",
        body = "Con las dos cartas en la mesa, **girá o agitá el celular**: el acelerómetro lo detecta, " +
            "el teléfono vibra y las cartas se dan vuelta.",
        tip = "Si no podés mover el teléfono, también podés **tocar el aviso** que aparece en pantalla.",
        preview = { GuerraShakePreview() },
    ),
    HowToSlide(
        emoji = "🧮",
        title = "¿Quién gana la ronda?",
        body = "Se comparan las 3 categorías una por una. Quien gane **más categorías** (2 o 3) gana la " +
            "ronda y suma la **diferencia** de las categorías que ganó. Si empatan en categorías, " +
            "cada uno suma la diferencia de la que ganó.",
        tip = "¡Ojo! Sacar mucha ventaja en una sola categoría no alcanza: en el ejemplo la merluza " +
            "arrasa en vitalidad, pero los ravioles ganan 2 de 3 y se llevan la ronda.",
        preview = { GuerraRoundPreview() },
    ),
    HowToSlide(
        emoji = "🏁",
        title = "Fin de la partida",
        body = "Después de la 3.ª ronda tocá **FINALIZAR PARTIDA**: gana quien tenga más puntos " +
            "(también puede haber **empate**).",
        tip = "Si salís en medio de una partida, se guarda sola y podés retomarla con " +
            "**CONTINUAR PARTIDA** desde esta pantalla.",
        preview = { GuerraEndPreview() },
    ),
    HowToSlide(
        emoji = "🌎",
        title = "Ranking global",
        body = "Al terminar, tu resultado se envía al ranking online. **Solo entran las partidas ganadas**, " +
            "ordenadas de mayor a menor puntaje, y se muestran las **10 mejores** de todos los jugadores. " +
            "Tus partidas aparecen destacadas.",
        tip = "Lo encontrás en **Perfil → 🏆 Ver ranking** (necesitás conexión a internet). " +
            "Si superás tu récord, se actualiza tu **Mejor puntaje**.",
        preview = { GuerraRankingPreview() },
    ),
)

// =====================================================================
//  Previews de Lunch Time
// =====================================================================

private data class FoodSample(@DrawableRes val img: Int, val name: String)

@Composable
private fun LunchTrayPreview() {
    val colors = AppTheme.colors
    val foods = remember {
        listOf(
            FoodSample(R.drawable.img_01_desayuno_tostadas_palta, "Tostadas"),
            FoodSample(R.drawable.img_05_desayuno_licuado_verde, "Licuado"),
            FoodSample(R.drawable.img_03_desayuno_yogur_griego, "Yogur"),
            FoodSample(R.drawable.img_07_desayuno_donas_glaseadas, "Donas"),
        )
    }
    var selected by remember { mutableStateOf(setOf(0, 1)) }
    var seconds by remember { mutableIntStateOf(20) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            seconds = if (seconds <= 1) 20 else seconds - 1
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Pill("● DESAYUNO", colors.badgeGreenBg)
            Pill("⏱ ${seconds}s", if (seconds <= 5) colors.error else colors.badgeBlueBg)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            foods.forEachIndexed { i, food ->
                val isSel = i in selected
                FoodTile(
                    food = food,
                    selected = isSel,
                    onClick = {
                        selected = when {
                            isSel -> selected - i
                            selected.size < 3 -> selected + i
                            else -> selected
                        }
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(colors.cardUnselectedBg)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("TU BANDEJA", color = colors.textSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(
                "Bandeja ${selected.size}/3",
                color = if (selected.size == 3) colors.success else colors.textPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
            )
        }
    }
}

@Composable
private fun FoodTile(
    food: FoodSample,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(
                    if (selected) 2.dp else 1.dp,
                    if (selected) colors.success else colors.cardBorder,
                    RoundedCornerShape(10.dp),
                ),
        ) {
            Image(
                painter = painterResource(food.img),
                contentDescription = food.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            if (selected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(3.dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(colors.success),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(food.name, color = colors.textPrimary, fontSize = 10.sp, maxLines = 1)
    }
}

@Composable
private fun LunchModePreview() {
    val colors = AppTheme.colors
    val actual = remember { Comida.obtenerPorHora() }
    var automatico by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ModeCard("⏰", "AUTOMÁTICO", "Según la hora", automatico, Modifier.weight(1f)) { automatico = true }
            ModeCard("🍽️", "YO ELIJO", "Vos decidís", !automatico, Modifier.weight(1f)) { automatico = false }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Comida.entries.forEach { comida ->
                val isActual = automatico && comida == actual
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isActual) colors.success.copy(alpha = 0.25f) else colors.cardUnselectedBg)
                        .border(
                            1.dp,
                            if (isActual) colors.success else colors.cardBorder,
                            RoundedCornerShape(10.dp),
                        )
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(comida.titulo, color = colors.textPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(comida.horario, color = colors.textSecondary, fontSize = 8.sp, maxLines = 1)
                }
            }
        }
        Text(
            text = if (automatico) "Ahora te toca: ${actual.titulo.uppercase()}" else "Elegí la comida que quieras armar",
            color = colors.textHighlight,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ModeCard(
    emoji: String,
    title: String,
    subtitle: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val colors = AppTheme.colors
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) colors.cardSelectedBg else colors.cardUnselectedBg)
            .border(
                if (selected) 2.dp else 1.dp,
                if (selected) colors.success else colors.cardBorder,
                RoundedCornerShape(14.dp),
            )
            .clickable(onClick = onClick)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(emoji, fontSize = 22.sp)
        Text(title, color = colors.textPrimary, fontSize = 11.sp, fontWeight = FontWeight.Black)
        Text(subtitle, color = colors.textSecondary, fontSize = 9.sp)
    }
}

@Composable
private fun LunchPointsPreview() {
    val colors = AppTheme.colors
    val categories = listOf("❤️ Salud" to 5, "🍞 Saciedad" to 4, "⚡ Vitalidad" to 3)
    var step by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            step = 0
            repeat(6) {
                delay(300)
                step++
            }
            delay(1800)
        }
    }
    val total = categories.sumOf { minOf(it.second, step) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        Text(
            "Cada alimento: hasta +2 en cada categoría",
            color = colors.textSecondary,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        categories.forEach { (label, value) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(label, color = colors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(92.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.weight(1f)) {
                    repeat(6) { i ->
                        val filled = i < minOf(value, step)
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(if (filled) colors.success else colors.cardUnselectedBg)
                                .border(1.dp, colors.cardBorder, CircleShape),
                        )
                    }
                }
                Text("${minOf(value, step)}/6", color = colors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
        }
        Text(
            "TOTAL: $total / 18 pts",
            color = colors.textHighlight,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun LunchResultPreview() {
    val colors = AppTheme.colors
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ResultSample(R.drawable.img_resultado_victoria, "11 a 18 pts", "¡PARTIDA GANADA! 🎉", colors.success, Modifier.weight(1f))
            ResultSample(R.drawable.img_resultado_derrota, "0 a 10 pts", "¡PARTIDA PERDIDA! ⚠️", colors.error, Modifier.weight(1f))
        }
        Text(
            "60% de 18 = 11 puntos para ganar",
            color = colors.textHighlight,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ResultSample(
    @DrawableRes img: Int,
    range: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, color.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
            .background(colors.cardUnselectedBg)
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(img),
            contentDescription = label,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(8.dp)),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(range, color = color, fontSize = 13.sp, fontWeight = FontWeight.Black)
        Text(label, color = colors.textPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Composable
private fun LunchStreakPreview() {
    val colors = AppTheme.colors
    val days = listOf("Dom" to false, "Lun" to true, "Mar" to true, "Mié" to true)
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(colors.cardUnselectedBg)
                .border(1.dp, colors.cardBorder, RoundedCornerShape(14.dp))
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("3", color = colors.success, fontSize = 36.sp, fontWeight = FontWeight.Black)
            Text("días sin ultraprocesados", color = colors.textSecondary, fontSize = 11.sp)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            days.forEach { (day, ok) ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background((if (ok) colors.success else colors.error).copy(alpha = 0.2f))
                        .border(1.dp, if (ok) colors.success else colors.error, RoundedCornerShape(10.dp))
                        .padding(vertical = 5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(day, color = colors.textPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(if (ok) "✓ 4+/6" else "✗ racha", color = if (ok) colors.success else colors.error, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Text(
            "Todas las partidas del día con 4 o más en ❤️ 🍞 ⚡",
            color = colors.textSecondary,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
        )
    }
}

private enum class DemoNivel { VERDE, AMARILLO, ROJO, SIN_DATOS }

@Composable
private fun LunchWeekPreview() {
    val colors = AppTheme.colors
    val labels = listOf("L", "M", "M", "J", "V", "S", "D")
    val names = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    val niveles = listOf(
        DemoNivel.VERDE, DemoNivel.VERDE, DemoNivel.AMARILLO, DemoNivel.ROJO,
        DemoNivel.VERDE, DemoNivel.SIN_DATOS, DemoNivel.SIN_DATOS,
    )
    val peorPartida = listOf("15/18", "14/18", "9/18", "4/18", "16/18", "", "")
    var selected by remember { mutableIntStateOf(2) }

    fun colorOf(n: DemoNivel) = when (n) {
        DemoNivel.VERDE -> colors.success
        DemoNivel.AMARILLO -> colors.warning
        DemoNivel.ROJO -> colors.error
        DemoNivel.SIN_DATOS -> colors.cardBorder
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        Text(
            "DÍAS SIN ULTRAPROCESADOS · ESTA SEMANA",
            color = colors.textSecondary,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            labels.forEachIndexed { i, label ->
                val isSel = i == selected
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selected = i },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(colorOf(niveles[i]))
                            .border(
                                if (isSel) 3.dp else 0.dp,
                                if (isSel) colors.textPrimary else Color.Transparent,
                                RoundedCornerShape(8.dp),
                            ),
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(label, color = colors.textPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        val nivel = niveles[selected]
        Text(
            text = if (nivel == DemoNivel.SIN_DATOS) {
                "${names[selected]}: todavía no jugaste ⬜"
            } else {
                "${names[selected]}: tu peor partida sumó ${peorPartida[selected]}"
            },
            color = if (nivel == DemoNivel.SIN_DATOS) colors.textSecondary else colorOf(nivel),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            LegendItem(colors.success, "13-18")
            LegendItem(colors.warning, "6-12")
            LegendItem(colors.error, "0-5")
            LegendItem(colors.cardBorder, "sin jugar")
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, color = AppTheme.colors.textSecondary, fontSize = 9.sp)
    }
}

// =====================================================================
//  Previews de Guerra de Platillos
// =====================================================================

/** Cartas reales de assets/cartas.json usadas en los ejemplos. */
private data class CardSample(
    val name: String,
    @DrawableRes val img: Int,
    val e: Int,
    val s: Int,
    val v: Int,
)

private val MERLUZA = CardSample("Merluza con puré", R.drawable.img_merluza_con_pure, 70, 80, 85)
private val RAVIOLES = CardSample("Ravioles", R.drawable.img_ravioles, 100, 85, 5)
private val MEDIALUNAS = CardSample("Medialunas", R.drawable.img_medialunas, 95, 50, 15)
private val AVENA = CardSample("Avena", R.drawable.img_avena, 80, 95, 90)
private val PANCHO = CardSample("Pancho", R.drawable.img_pancho, 60, 30, 15)

@Composable
private fun trafficColor(value: Int): Color {
    val colors = AppTheme.colors
    return when {
        value <= 30 -> colors.error
        value <= 65 -> colors.warning
        else -> colors.success
    }
}

@Composable
private fun TutorialCard(
    card: CardSample,
    width: Dp,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
) {
    val colors = AppTheme.colors
    val big = width >= 100.dp
    Column(
        modifier = modifier
            .width(width)
            .clip(RoundedCornerShape(10.dp))
            .background(colors.cardUnselectedBg)
            .border(
                if (highlighted) 2.dp else 1.dp,
                if (highlighted) colors.success else colors.cardBorder,
                RoundedCornerShape(10.dp),
            )
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(card.img),
            contentDescription = card.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.25f)
                .clip(RoundedCornerShape(7.dp)),
        )
        Text(
            card.name,
            color = colors.textPrimary,
            fontSize = if (big) 11.sp else 8.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(vertical = 2.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            StatBox("E", card.e, big)
            StatBox("S", card.s, big)
            StatBox("V", card.v, big)
        }
    }
}

@Composable
private fun RowScope.StatBox(letter: String, value: Int, big: Boolean) {
    val c = trafficColor(value)
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(4.dp))
            .background(c.copy(alpha = 0.18f)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(letter, color = AppTheme.colors.textSecondary, fontSize = if (big) 9.sp else 7.sp)
        Text("$value", color = c, fontSize = if (big) 13.sp else 8.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun CardBack(width: Dp, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.img_card_cover),
        contentDescription = "Carta boca abajo",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .width(width)
            .aspectRatio(0.67f)
            .clip(RoundedCornerShape(10.dp)),
    )
}

@Composable
private fun GuerraTablePreview() {
    val colors = AppTheme.colors
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Pill("VOS: 0 pts", colors.badgeGreenBg)
            Pill("Ronda 0/3", colors.cardUnselectedBg)
            Pill("RIVAL: 0 pts", colors.badgeOrangeBg)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(3) { CardBack(width = 30.dp) }
        }
        Text("VS", color = colors.success, fontSize = 18.sp, fontWeight = FontWeight.Black)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            TutorialCard(AVENA, width = 58.dp)
            TutorialCard(PANCHO, width = 58.dp)
            TutorialCard(MEDIALUNAS, width = 58.dp)
        }
        Text("Tu mano", color = colors.textSecondary, fontSize = 9.sp)
    }
}

@Composable
private fun GuerraCardPreview() {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TutorialCard(MEDIALUNAS, width = 110.dp, highlighted = true)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            StatLegend("E", "Energía", MEDIALUNAS.e)
            StatLegend("S", "Saciedad", MEDIALUNAS.s)
            StatLegend("V", "Vitalidad", MEDIALUNAS.v)
            Text("Tocá → ver detalle", color = colors.textSecondary, fontSize = 9.sp)
        }
    }
}

@Composable
private fun StatLegend(letter: String, name: String, value: Int) {
    val c = trafficColor(value)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(c.copy(alpha = 0.2f))
                .border(1.dp, c, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(letter, color = c, fontSize = 12.sp, fontWeight = FontWeight.Black)
        }
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(name, color = AppTheme.colors.textPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("$value", color = c, fontSize = 11.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun GuerraDragPreview() {
    val colors = AppTheme.colors
    val transition = rememberInfiniteTransition(label = "drag")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1600, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "dragProgress",
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .width(38.dp)
                        .aspectRatio(0.67f)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, colors.success.copy(alpha = 0.7f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Tu\ncarta", color = colors.textSecondary, fontSize = 8.sp, textAlign = TextAlign.Center)
                }
            }
            Text("VS", color = colors.success, fontSize = 14.sp, fontWeight = FontWeight.Black)
            CardBack(width = 38.dp)
        }
        Text(
            "⬆",
            color = colors.success,
            fontSize = 22.sp,
            modifier = Modifier
                .offset(y = (-6 * progress).dp)
                .alpha(1f - progress * 0.6f),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            TutorialCard(AVENA, width = 54.dp)
            TutorialCard(
                PANCHO,
                width = 54.dp,
                highlighted = true,
                modifier = Modifier
                    .offset(y = (-40 * progress).dp)
                    .alpha(1f - progress * 0.5f),
            )
            TutorialCard(MEDIALUNAS, width = 54.dp)
        }
        Text("● Arrastrá hacia arriba para jugar · Tocá para detalle", color = colors.textSecondary, fontSize = 8.sp)
    }
}

@Composable
private fun GuerraShakePreview() {
    val colors = AppTheme.colors
    val transition = rememberInfiniteTransition(label = "shake")
    val angle by transition.animateFloat(
        initialValue = -18f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(tween(140, easing = LinearEasing), RepeatMode.Reverse),
        label = "shakeAngle",
    )
    var revealed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1800)
            revealed = !revealed
        }
    }
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "📱",
                fontSize = 46.sp,
                modifier = Modifier.rotate(if (revealed) 0f else angle),
            )
            Text(
                if (revealed) "¡Reveladas!" else "📳 Agitando…",
                color = if (revealed) colors.success else colors.warning,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            if (revealed) {
                TutorialCard(MERLUZA, width = 62.dp)
                Text("VS", color = colors.success, fontSize = 12.sp, fontWeight = FontWeight.Black)
                TutorialCard(RAVIOLES, width = 62.dp)
            } else {
                CardBack(width = 62.dp)
                Text("VS", color = colors.success, fontSize = 12.sp, fontWeight = FontWeight.Black)
                CardBack(width = 62.dp)
            }
        }
    }
}

@Composable
private fun GuerraRoundPreview() {
    val colors = AppTheme.colors
    val rows = listOf(
        Triple("E", MERLUZA.e, RAVIOLES.e),
        Triple("S", MERLUZA.s, RAVIOLES.s),
        Triple("V", MERLUZA.v, RAVIOLES.v),
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("", modifier = Modifier.width(24.dp))
            Text("Merluza (vos)", color = colors.textPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text("Ravioles (rival)", color = colors.textPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text("Gana", color = colors.textSecondary, fontSize = 10.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        }
        rows.forEach { (letter, mine, theirs) ->
            val iWin = mine > theirs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.cardUnselectedBg)
                    .padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(letter, color = colors.textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Black, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                ValueCell(mine, winner = iWin, modifier = Modifier.weight(1f))
                ValueCell(theirs, winner = !iWin, modifier = Modifier.weight(1f))
                Text(
                    if (iWin) "Vos (+${mine - theirs})" else "Rival +${theirs - mine}",
                    color = if (iWin) colors.success else colors.error,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
            }
        }
        Text(
            "Rival gana 2 de 3 categorías → Rival +35 · Vos +0",
            color = colors.error,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(colors.error.copy(alpha = 0.12f))
                .padding(6.dp),
        )
    }
}

@Composable
private fun ValueCell(value: Int, winner: Boolean, modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    Text(
        "$value",
        color = if (winner) colors.textHighlight else colors.textSecondary,
        fontSize = if (winner) 15.sp else 12.sp,
        fontWeight = if (winner) FontWeight.Black else FontWeight.Normal,
        textAlign = TextAlign.Center,
        modifier = modifier,
    )
}

@Composable
private fun GuerraEndPreview() {
    val colors = AppTheme.colors
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Pill("Ronda 3/3", colors.cardUnselectedBg)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            ScoreBlock("VOS", 145, colors.success)
            Text("VS", color = colors.success, fontSize = 16.sp, fontWeight = FontWeight.Black)
            ScoreBlock("RIVAL", 90, colors.error)
        }
        Text("¡GANASTE LA PARTIDA!", color = colors.success, fontSize = 16.sp, fontWeight = FontWeight.Black)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Pill("💾 Se guarda sola", colors.badgeBlueBg)
            Pill("CONTINUAR PARTIDA ▷", colors.badgeGreenBg)
        }
    }
}

@Composable
private fun ScoreBlock(label: String, score: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = AppTheme.colors.textSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text("$score", color = color, fontSize = 26.sp, fontWeight = FontWeight.Black)
        Text("pts", color = AppTheme.colors.textSecondary, fontSize = 9.sp)
    }
}

@Composable
private fun GuerraRankingPreview() {
    val colors = AppTheme.colors
    val entries = listOf(
        Triple("🥇", "mateo_13", 210),
        Triple("🥈", "vos", 185),
        Triple("🥉", "sofi.verde", 160),
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        Text(
            "🏆 RANKING GLOBAL · TOP 10",
            color = colors.textHighlight,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        entries.forEach { (medal, name, score) ->
            val isMe = name == "vos"
            RankingRow(
                medal = medal,
                name = if (isMe) "vos ⭐" else name,
                score = "$score",
                bg = if (isMe) colors.success.copy(alpha = 0.18f) else colors.cardUnselectedBg,
                border = if (isMe) colors.success else colors.cardBorder,
            )
        }
        RankingRow(
            medal = "✗",
            name = "Partida perdida · no entra",
            score = "120",
            bg = colors.cardUnselectedBg,
            border = colors.error.copy(alpha = 0.6f),
            faded = true,
        )
    }
}

@Composable
private fun RankingRow(
    medal: String,
    name: String,
    score: String,
    bg: Color,
    border: Color,
    faded: Boolean = false,
) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (faded) 0.6f else 1f)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(medal, fontSize = 14.sp, color = if (faded) colors.error else colors.textPrimary, modifier = Modifier.width(26.dp))
        Text(
            name,
            color = if (faded) colors.error else colors.textPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            score,
            color = if (faded) colors.textSecondary else colors.textHighlight,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            textDecoration = if (faded) TextDecoration.LineThrough else null,
        )
    }
}

// =====================================================================
//  Helpers
// =====================================================================

@Composable
private fun Pill(text: String, bg: Color, fontSize: TextUnit = 10.sp) {
    Text(
        text = text,
        color = AppTheme.colors.textPrimary,
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}
