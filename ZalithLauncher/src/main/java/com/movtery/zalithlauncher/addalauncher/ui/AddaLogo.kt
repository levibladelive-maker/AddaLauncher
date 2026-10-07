package com.movtery.zalithlauncher.addalauncher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movtery.zalithlauncher.addalauncher.studio.AddaStudio

/** AddaLauncher logo mark: gradient block with an "A", glowing when enabled. */
@Composable
fun AddaLogo(size: Dp = 56.dp, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(size * 0.28f)
    Box(
        modifier
            .size(size)
            .then(if (AddaStudio.glowOn) Modifier.shadow(size * 0.35f, shape, ambientColor = AddaStudio.secondary, spotColor = AddaStudio.primary) else Modifier)
            .background(Brush.linearGradient(listOf(AddaStudio.primary, AddaStudio.secondary)), shape),
        contentAlignment = Alignment.Center
    ) {
        Text("A", color = Color.White, fontWeight = FontWeight.Black, fontSize = (size.value * 0.55f).sp)
    }
}
