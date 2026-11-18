package com.daa.guerradeplatillos.ui.screens.deck

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.daa.guerradeplatillos.R
import com.daa.guerradeplatillos.data.DeckRepository
import com.daa.guerradeplatillos.model.CardModel
import com.daa.guerradeplatillos.ui.components.CircleIconButton
import com.daa.guerradeplatillos.ui.components.PrimaryButton
import com.daa.guerradeplatillos.ui.components.ScreenContainer
import com.daa.guerradeplatillos.ui.screens.game.cardDrawableRes
import com.daa.guerradeplatillos.ui.theme.AppTheme
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.delay
import java.io.File
import java.util.concurrent.Executors

private const val QR_PREFIX = "codigoverde:"
private const val PRINTABLE_DECK_QR = "codigoverde:baraja_imprimible"
private const val PRINTABLE_DECK_ASSET = "baraja_imprimible.pdf"
private const val MISS_FRAMES_TO_REARM = 4

enum class QrScanMode {
    CARD_DETAIL,
    PRINTABLE_DECK,
}

private data class DeckAlertState(
    val title: String,
    val message: String,
)

/**
 * Pantalla de escáner QR con CameraX + ML Kit:
 * - [QrScanMode.CARD_DETAIL]: abre el detalle de una carta (`codigoverde:{id}`).
 * - [QrScanMode.PRINTABLE_DECK]: valida `codigoverde:baraja_imprimible` y abre el PDF local.
 */
@Composable
fun QrScannerScreen(
    mode: QrScanMode = QrScanMode.CARD_DETAIL,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val colors = AppTheme.colors

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val deckById = remember(context) {
        DeckRepository.cargarBaraja(context).associateBy { it.id.lowercase() }
    }
    var scannedCard by remember { mutableStateOf<CardModel?>(null) }
    var deckAlert by remember { mutableStateOf<DeckAlertState?>(null) }
    var lastHandledQr by remember { mutableStateOf<String?>(null) }
    var ignoredQr by remember { mutableStateOf<String?>(null) }
    var missFrames by remember { mutableIntStateOf(0) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    val isOverlayOpen = scannedCard != null || deckAlert != null

    val dismissOverlay = {
        ignoredQr = lastHandledQr
        missFrames = 0
        scannedCard = null
        deckAlert = null
        feedbackMessage = null
    }

    BackHandler(enabled = isOverlayOpen) {
        dismissOverlay()
    }

    LaunchedEffect(feedbackMessage) {
        if (feedbackMessage != null) {
            delay(2500L)
            feedbackMessage = null
        }
    }

    if (!hasCameraPermission) {
        ScreenContainer {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircleIconButton(
                        icon = R.drawable.ic_arrow_back,
                        contentDescription = stringResource(R.string.btn_back_description),
                        onClick = onBack,
                    )
                    CircleIconButton(
                        icon = R.drawable.ic_settings,
                        contentDescription = stringResource(R.string.btn_settings_description),
                        onClick = onSettingsClick,
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.cardBg)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Permiso de cámara requerido",
                        color = colors.textHighlight,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = if (mode == QrScanMode.PRINTABLE_DECK) {
                            "Necesitamos acceso a la cámara para escanear el código QR de la baraja imprimible."
                        } else {
                            "Necesitamos acceso a la cámara para escanear los códigos QR de las cartas."
                        },
                        color = colors.textSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(16.dp))
                    PrimaryButton(
                        text = "Permitir cámara",
                        onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        QrCameraPreview(
            isPaused = isOverlayOpen,
            onFrameResult = { rawValue ->
                if (isOverlayOpen) return@QrCameraPreview

                val normalized = rawValue?.trim()
                if (ignoredQr != null) {
                    if (normalized != null && normalized.equals(ignoredQr, ignoreCase = true)) {
                        missFrames = 0
                        return@QrCameraPreview
                    }
                    missFrames += 1
                    if (missFrames < MISS_FRAMES_TO_REARM && normalized == null) {
                        return@QrCameraPreview
                    }
                    ignoredQr = null
                    missFrames = 0
                }

                if (normalized.isNullOrEmpty()) return@QrCameraPreview

                when (mode) {
                    QrScanMode.CARD_DETAIL -> {
                        if (!normalized.startsWith(QR_PREFIX, ignoreCase = true)) {
                            feedbackMessage = "Este QR no pertenece a Código Verde"
                            return@QrCameraPreview
                        }
                        val cardId = normalized.substring(QR_PREFIX.length).trim().lowercase()
                        val found = deckById[cardId]
                        if (found != null) {
                            feedbackMessage = null
                            lastHandledQr = normalized
                            scannedCard = found
                        } else {
                            feedbackMessage = "Carta no encontrada: $cardId"
                        }
                    }

                    QrScanMode.PRINTABLE_DECK -> {
                        lastHandledQr = normalized
                        if (normalized.equals(PRINTABLE_DECK_QR, ignoreCase = true)) {
                            ignoredQr = normalized
                            missFrames = 0
                            val error = abrirBarajaPdf(context)
                            if (error != null) {
                                deckAlert = DeckAlertState(
                                    title = "No se pudo abrir el PDF",
                                    message = error,
                                )
                            }
                        } else {
                            deckAlert = DeckAlertState(
                                title = "¡Ups! Baraja no encontrada 🃏",
                                message = "Ese código QR no corresponde a la baraja imprimible de Código Verde.",
                            )
                        }
                    }
                }
            },
        )

        if (!isOverlayOpen) {
            Box(
                modifier = Modifier
                    .size(230.dp)
                    .align(Alignment.Center)
                    .border(3.dp, colors.success, RoundedCornerShape(20.dp)),
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 32.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.background.copy(alpha = 0.88f))
                    .border(
                        width = 1.dp,
                        color = if (feedbackMessage != null) colors.error else colors.cardBorder,
                        shape = RoundedCornerShape(14.dp),
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Text(
                    text = feedbackMessage ?: if (mode == QrScanMode.PRINTABLE_DECK) {
                        "Apuntá al QR de la baraja imprimible"
                    } else {
                        "Apuntá al código QR de una carta"
                    },
                    color = if (feedbackMessage != null) colors.error else colors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        }

        // Overlay de detalle de carta (modo CARD_DETAIL)
        scannedCard?.let { card ->
            val maxCardHeight = (LocalConfiguration.current.screenHeightDp * 0.72f).dp
            val fullRes = cardDrawableRes(card.id, mini = false)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.72f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        dismissOverlay()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .widthIn(max = 360.dp)
                        .heightIn(max = maxCardHeight)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { /* evita cerrar al tocar la carta */ },
                ) {
                    if (fullRes != null) {
                        Image(
                            painter = painterResource(fullRes),
                            contentDescription = card.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .background(colors.cardBg),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = card.name,
                                color = colors.textPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    CircleIconButton(
                        icon = R.drawable.ic_close,
                        contentDescription = stringResource(R.string.btn_close_description),
                        onClick = dismissOverlay,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                    )
                }
            }
        }

        // Alerta amigable (modo PRINTABLE_DECK cuando el QR no coincide o falla el visor)
        deckAlert?.let { alert ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.72f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        dismissOverlay()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 28.dp)
                        .widthIn(max = 340.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.cardBg)
                        .border(1.5.dp, colors.warning, RoundedCornerShape(20.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { /* evita cerrar al tocar la tarjeta */ }
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = alert.title,
                        color = colors.warning,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = alert.message,
                        color = colors.textPrimary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(18.dp))
                    PrimaryButton(
                        text = "Intentar de nuevo",
                        onClick = dismissOverlay,
                        minHeight = 46.dp,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        // Cabecera siempre por encima (permite volver incluso con un overlay abierto)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleIconButton(
                icon = R.drawable.ic_arrow_back,
                contentDescription = stringResource(R.string.btn_back_description),
                onClick = onBack,
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.background.copy(alpha = 0.85f))
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp),
            ) {
                Text(
                    text = if (mode == QrScanMode.PRINTABLE_DECK) "Imprimir baraja" else "Escanear carta",
                    color = colors.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            CircleIconButton(
                icon = R.drawable.ic_settings,
                contentDescription = stringResource(R.string.btn_settings_description),
                onClick = onSettingsClick,
            )
        }
    }
}

private fun abrirBarajaPdf(context: Context): String? {
    return try {
        val outFile = File(context.cacheDir, PRINTABLE_DECK_ASSET)
        context.assets.open(PRINTABLE_DECK_ASSET).use { input ->
            outFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            outFile,
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(intent)
        null
    } catch (_: ActivityNotFoundException) {
        "No encontramos una app instalada para abrir archivos PDF."
    } catch (_: Exception) {
        "Ocurrió un problema al preparar el archivo PDF de la baraja."
    }
}

@OptIn(ExperimentalGetImage::class)
@Composable
private fun QrCameraPreview(
    isPaused: Boolean,
    onFrameResult: (String?) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentIsPaused by rememberUpdatedState(isPaused)
    val currentOnFrameResult by rememberUpdatedState(onFrameResult)
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val previewView = remember { PreviewView(context) }
    val scanner = remember {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        BarcodeScanning.getClient(options)
    }

    DisposableEffect(lifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val listener = Runnable {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }
            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { imageAnalysis ->
                    imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                        val mediaImage = imageProxy.image
                        if (currentIsPaused || mediaImage == null) {
                            imageProxy.close()
                            return@setAnalyzer
                        }
                        val inputImage = InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees,
                        )
                        scanner.process(inputImage)
                            .addOnSuccessListener { barcodes ->
                                val raw = barcodes.firstOrNull { !it.rawValue.isNullOrBlank() }?.rawValue
                                currentOnFrameResult(raw)
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    }
                }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                analysis,
            )
        }
        cameraProviderFuture.addListener(listener, ContextCompat.getMainExecutor(context))

        onDispose {
            runCatching { cameraProviderFuture.get().unbindAll() }
            scanner.close()
            cameraExecutor.shutdown()
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize(),
    )
}
