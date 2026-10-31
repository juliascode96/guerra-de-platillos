package com.daa.guerradeplatillos.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daa.guerradeplatillos.R
import com.daa.guerradeplatillos.ui.components.BadgePill
import com.daa.guerradeplatillos.ui.components.CircleIconButton
import com.daa.guerradeplatillos.ui.components.ScreenContainer
import com.daa.guerradeplatillos.ui.components.TrafficLightDots
import com.daa.guerradeplatillos.ui.theme.AppTheme
import com.daa.guerradeplatillos.ui.theme.Dimens
import com.daa.guerradeplatillos.ui.theme.GuerraDePlatillosTheme

/** Pantalla principal (equivalente a activity_main.xml de Código Verde). */
@Composable
fun HomeScreen(
    onPlayClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val colors = AppTheme.colors

    ScreenContainer {
        // Botón Configuración (esquina superior derecha)
        CircleIconButton(
            icon = R.drawable.ic_settings,
            contentDescription = stringResource(R.string.btn_settings_description),
            onClick = onSettingsClick,
            modifier = Modifier.align(Alignment.TopEnd)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.weight(1f))

            // Imagen principal / Logo (reemplazar res/drawable-nodpi/logo_app.png)
            Image(
                painter = painterResource(R.drawable.logo_app),
                contentDescription = stringResource(R.string.desc_logo),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(260.dp)
            )

            Spacer(Modifier.height(32.dp))

            // Título con la segunda palabra resaltada
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = colors.textPrimary)) {
                        append(stringResource(R.string.home_title_part1))
                    }
                    withStyle(SpanStyle(color = colors.textHighlight)) {
                        append(stringResource(R.string.home_title_part2))
                    }
                },
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(Dimens.SpacingSmall))

            Text(
                text = stringResource(R.string.home_subtitle),
                style = MaterialTheme.typography.titleLarge,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.weight(1f))

            TrafficLightDots(modifier = Modifier.padding(bottom = Dimens.SpacingLarge))

            // Badge inferior: abre el juego
            BadgePill(
                text = stringResource(R.string.home_badge),
                onClick = onPlayClick,
                modifier = Modifier.padding(bottom = 48.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    GuerraDePlatillosTheme {
        Box { HomeScreen(onPlayClick = {}, onSettingsClick = {}) }
    }
}
