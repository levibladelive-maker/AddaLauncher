package com.movtery.zalithlauncher.addalauncher.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.movtery.zalithlauncher.addalauncher.animations.AddaBackground
import com.movtery.zalithlauncher.addalauncher.studio.AddaPerfMode
import com.movtery.zalithlauncher.addalauncher.studio.AddaStudio

private val Swatches = listOf(
    0xFF7C4DFF, 0xFF00E5FF, 0xFFFF4081, 0xFF00E676, 0xFFFFD740, 0xFFFF6E40, 0xFF448AFF, 0xFFE040FB
).map { Color(it) }

/** AddaLauncher Studio: live preview on the right, customization controls on the left. */
@Composable
fun AddaStudioDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize().background(Color(0xFF0B0716))) {
            AddaBackground()
            Row(Modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(
                    Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.45f)).padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("AddaLauncher Studio", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                    ColorPick("Primary color", AddaStudio.primary, AddaStudio::setPrimary)
                    ColorPick("Secondary color", AddaStudio.secondary, AddaStudio::setSecondary)
                    ColorPick("Accent color", AddaStudio.accent, AddaStudio::setAccent)
                    Toggle("Particles", AddaStudio.particlesOn, AddaStudio::setParticles)
                    Toggle("Glow", AddaStudio.glowOn, AddaStudio::setGlow)
                    Toggle("Moving lights", AddaStudio.movingLightsOn, AddaStudio::setMovingLights)
                    Toggle("Reduced animations", AddaStudio.reducedAnimations, AddaStudio::setReducedAnimations)
                    Toggle("Reduced particles", AddaStudio.reducedParticles, AddaStudio::setReducedParticles)
                    Toggle("Music (preference only)", AddaStudio.musicOn, AddaStudio::setMusic)
                    Text("Performance mode", color = Color.White, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AddaPerfMode.entries.forEach { m ->
                            FilterChip(AddaStudio.perfMode == m, { AddaStudio.setPerfMode(m) }, { Text(m.label) })
                        }
                    }
                    Text("Loading messages (one per line)", color = Color.White, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = AddaStudio.loadingMessages,
                        onValueChange = AddaStudio::setLoadingMessages,
                        modifier = Modifier.fillMaxWidth(), minLines = 4, maxLines = 8
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton({ AddaStudio.resetColors() }) { Text("Reset colors") }
                        OutlinedButton({ AddaStudio.setLoadingMessages(AddaStudio.DEFAULT_MESSAGES) }) { Text("Reset messages") }
                        Button(onDismiss) { Text("Done") }
                    }
                }
                // Live preview
                Box(
                    Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(20.dp))
                        .border(BorderStroke(1.dp, AddaStudio.secondary.copy(alpha = 0.6f)), RoundedCornerShape(20.dp))
                ) {
                    AddaBackground()
                    Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        AddaLogo(72.dp)
                        Text("AddaLauncher", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                        Text("Live preview", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                        Box(
                            Modifier.padding(top = 14.dp).clip(RoundedCornerShape(14.dp))
                                .background(androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(AddaStudio.primary, AddaStudio.accent)))
                                .padding(horizontal = 28.dp, vertical = 10.dp)
                        ) { Text("PLAY", color = Color.White, fontWeight = FontWeight.Black) }
                    }
                }
            }
        }
    }
}

@Composable
private fun Toggle(label: String, value: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.White)
        Switch(value, onChange)
    }
}

@Composable
private fun ColorPick(label: String, current: Color, onPick: (Color) -> Unit) {
    Column {
        Text(label, color = Color.White, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 6.dp)) {
            Swatches.forEach { c ->
                Box(
                    Modifier.size(28.dp).clip(CircleShape).background(c)
                        .border(BorderStroke(if (c == current) 3.dp else 0.dp, Color.White), CircleShape)
                        .clickable { onPick(c) }
                )
            }
        }
        Slider(
            value = hueOf(current), onValueChange = { onPick(Color.hsv(it, 0.75f, 1f)) },
            valueRange = 0f..360f
        )
    }
}

private fun hueOf(c: Color): Float {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(c.toArgb(), hsv)
    return hsv[0]
}
