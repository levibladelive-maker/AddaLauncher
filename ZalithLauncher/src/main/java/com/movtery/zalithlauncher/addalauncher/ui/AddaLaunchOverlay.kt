package com.movtery.zalithlauncher.addalauncher.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movtery.zalithlauncher.addalauncher.animations.AddaBackground
import com.movtery.zalithlauncher.addalauncher.studio.AddaStudio

/**
 * Cinematic AddaLauncher launch screen: logo, version, animated background,
 * progress bar, percentage and rotating status text (messages are editable in Studio).
 * The launch engine runs independently; this overlay is presentation only and
 * dismisses itself after [durationMs] so engine dialogs (account, Java, errors) stay reachable.
 */
@Composable
fun AddaLaunchOverlay(
    visible: Boolean,
    versionName: String,
    durationMs: Int = 7000,
    onFinished: () -> Unit
) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(visible) {
        if (visible) {
            progress.snapTo(0f)
            progress.animateTo(1f, tween(if (AddaStudio.reducedAnimations) durationMs / 2 else durationMs, easing = LinearEasing))
            onFinished()
        }
    }
    AnimatedVisibility(visible, enter = fadeIn(tween(350)), exit = fadeOut(tween(500))) {
        BackHandler {}
        Box(Modifier.fillMaxSize().background(Color.Black).clickable(enabled = true) {}) {
            AddaBackground()
            Column(
                Modifier.fillMaxSize().padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AddaLogo(88.dp)
                Spacer(Modifier.height(14.dp))
                Text("AddaLauncher", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp)
                Text("Minecraft $versionName", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                Spacer(Modifier.height(28.dp))
                val msgs = AddaStudio.messageList()
                val idx = (progress.value * msgs.size).toInt().coerceIn(0, msgs.lastIndex)
                Crossfade(msgs[idx], label = "adda_status") { m ->
                    Text(m, color = Color.White, fontSize = 15.sp)
                }
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { progress.value },
                    modifier = Modifier.width(320.dp).height(6.dp),
                    color = AddaStudio.secondary,
                    trackColor = Color.White.copy(alpha = 0.15f)
                )
                Spacer(Modifier.height(6.dp))
                Text("${(progress.value * 100).toInt()}%", color = AddaStudio.secondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
