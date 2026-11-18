package com.daa.guerradeplatillos.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.daa.guerradeplatillos.R
import com.daa.guerradeplatillos.ui.theme.AppTheme
import com.daa.guerradeplatillos.util.SoundManager
import kotlinx.coroutines.launch

/**
 * Una "diapositiva" del tutorial de un juego.
 *
 * - [body] y [tip] aceptan **negritas** con doble asterisco.
 * - [preview] dibuja una mini-maqueta de cómo se ve esa parte del juego.
 */
data class HowToSlide(
    val emoji: String,
    val title: String,
    val body: String,
    val tip: String? = null,
    val preview: @Composable BoxScope.() -> Unit,
)

/**
 * Pop-up de pantalla completa con las reglas de un juego, presentado como un
 * carrusel de diapositivas (HorizontalPager) que se puede deslizar o recorrer con botones.
 */
@Composable
fun HowToPlayDialog(
    gameTitle: String,
    slides: List<HowToSlide>,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        HowToPlayContent(gameTitle = gameTitle, slides = slides, onDismiss = onDismiss)
    }
}

@Composable
private fun HowToPlayContent(
    gameTitle: String,
    slides: List<HowToSlide>,
    onDismiss: () -> Unit,
) {
    val colors = AppTheme.colors
    val pagerState = rememberPagerState(pageCount = { slides.size })
    val scope = rememberCoroutineScope()
    val current = pagerState.currentPage
    val isLast = current == slides.lastIndex

    val goNext: () -> Unit = {
        if (isLast) onDismiss() else scope.launch { pagerState.animateScrollToPage(current + 1) }
    }
    val goPrev: () -> Unit = {
        scope.launch { pagerState.animateScrollToPage((current - 1).coerceAtLeast(0)) }
    }

    ScreenContainer {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isLandscape = maxWidth > maxHeight

            Column(modifier = Modifier.fillMaxSize()) {
                // Cabecera
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "❓ CÓMO JUGAR",
                            color = colors.textHighlight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = gameTitle,
                            color = colors.textPrimary,
                            fontSize = if (isLandscape) 18.sp else 22.sp,
                            fontWeight = FontWeight.Black,
                        )
                    }
                    CircleIconButton(
                        icon = R.drawable.ic_close,
                        contentDescription = "Cerrar instrucciones",
                        onClick = onDismiss,
                    )
                }

                Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 14.dp))

                HorizontalPager(
                    state = pagerState,
                    pageSpacing = 16.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) { page ->
                    SlidePage(
                        slide = slides[page],
                        index = page,
                        total = slides.size,
                        isLandscape = isLandscape,
                    )
                }

                Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 14.dp))

                if (isLandscape) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        NavButtons(
                            current = current,
                            isLast = isLast,
                            onPrev = goPrev,
                            onNext = goNext,
                            modifier = Modifier.weight(1f),
                        )
                        PageDots(pagerState = pagerState, total = slides.size)
                    }
                } else {
                    PageDots(
                        pagerState = pagerState,
                        total = slides.size,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    NavButtons(
                        current = current,
                        isLast = isLast,
                        onPrev = goPrev,
                        onNext = goNext,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun NavButtons(
    current: Int,
    isLast: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val compactPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (current > 0) {
            Button(
                onClick = {
                    SoundManager.playBubbly()
                    onPrev()
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.buttonSecondary,
                    contentColor = colors.buttonSecondaryText,
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                contentPadding = compactPadding,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
            ) {
                Text(
                    text = "← ANTERIOR",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Button(
            onClick = {
                SoundManager.playBubbly()
                onNext()
            },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.buttonPrimary,
                contentColor = colors.buttonPrimaryText,
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
            contentPadding = compactPadding,
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
        ) {
            Text(
                text = if (isLast) "¡A JUGAR! ▷" else "SIGUIENTE →",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun PageDots(
    pagerState: PagerState,
    total: Int,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val scope = rememberCoroutineScope()
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(total) { i ->
            val selected = pagerState.currentPage == i
            val w by animateDpAsState(targetValue = if (selected) 22.dp else 8.dp, label = "dotWidth")
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(w)
                    .clip(RoundedCornerShape(50))
                    .background(if (selected) colors.success else colors.cardBorder)
                    .clickable { scope.launch { pagerState.animateScrollToPage(i) } }
            )
        }
    }
}

@Composable
private fun SlidePage(
    slide: HowToSlide,
    index: Int,
    total: Int,
    isLandscape: Boolean,
) {
    if (isLandscape) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PreviewFrame(
                slide = slide,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )
            SlideText(
                slide = slide,
                index = index,
                total = total,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            PreviewFrame(
                slide = slide,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            SlideText(slide = slide, index = index, total = total)
        }
    }
}

@Composable
private fun PreviewFrame(slide: HowToSlide, modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(colors.cardBg)
            .border(1.dp, colors.success.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center,
    ) {
        slide.preview(this)
    }
}

@Composable
private fun SlideText(
    slide: HowToSlide,
    index: Int,
    total: Int,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    Column(modifier = modifier) {
        Text(
            text = "PASO ${index + 1} DE $total",
            color = colors.textPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(colors.badgeGreenBg)
                .padding(horizontal = 10.dp, vertical = 4.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = slide.emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = slide.title,
                color = colors.textHighlight,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 24.sp,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = boldMarkup(slide.body, colors.textHighlight),
            color = colors.textPrimary,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        )
        slide.tip?.let { tip ->
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.warning.copy(alpha = 0.12f))
                    .border(1.dp, colors.warning.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Text(text = "💡", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = boldMarkup(tip, colors.warning),
                    color = colors.textPrimary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

/** Convierte `**texto**` en negrita con color destacado. */
private fun boldMarkup(text: String, highlight: Color): AnnotatedString = buildAnnotatedString {
    text.split("**").forEachIndexed { i, part ->
        if (i % 2 == 1) {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = highlight)) { append(part) }
        } else {
            append(part)
        }
    }
}
