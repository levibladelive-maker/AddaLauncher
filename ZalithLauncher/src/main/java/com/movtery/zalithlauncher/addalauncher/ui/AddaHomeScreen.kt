package com.movtery.zalithlauncher.addalauncher.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movtery.zalithlauncher.addalauncher.animations.AddaBackground
import com.movtery.zalithlauncher.addalauncher.studio.AddaStudio
import com.movtery.zalithlauncher.game.account.AccountsManager
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.game.version.installed.VersionsManager

/** Navigation hooks supplied by MainScreen so this UI stays decoupled from the nav graph. */
class AddaHomeActions(
    val launch: (Version?) -> Unit,
    val openVersionDetails: (Version) -> Unit,
    val openVersionManager: () -> Unit,
    val openAccounts: () -> Unit,
    val openSettings: () -> Unit,
    val openDownloads: () -> Unit,
    val openFiles: () -> Unit,
    val openAbout: () -> Unit
)

private class Tile(val title: String, val subtitle: String, val glyph: String, val onClick: () -> Unit)

@Composable
fun AddaHomeScreen(actions: AddaHomeActions) {
    val context = LocalContext.current
    remember { AddaStudio.init(context); true }

    val versions by VersionsManager.versions.collectAsState()
    val current by VersionsManager.currentVersion.collectAsState()
    val account by AccountsManager.currentAccountFlow.collectAsState()
    var studio by remember { mutableStateOf(false) }
    var launching by remember { mutableStateOf(false) }
    var menu by remember { mutableStateOf(false) }

    val tiles = listOf(
        Tile("Version Manager", "Install & manage versions", "V", actions.openVersionManager),
        Tile("Mod Manager", "Mods for this profile", "M") { current?.let(actions.openVersionDetails) ?: actions.openVersionManager() },
        Tile("Resource Packs", "Textures & packs", "R") { current?.let(actions.openVersionDetails) ?: actions.openVersionManager() },
        Tile("Shaders", "Shader packs", "S") { current?.let(actions.openVersionDetails) ?: actions.openVersionManager() },
        Tile("Worlds", "Saves & backups", "W") { current?.let(actions.openVersionDetails) ?: actions.openVersionManager() },
        Tile("Java Manager", "Runtimes", "J", actions.openSettings),
        Tile("Performance", "Renderer & RAM", "P", actions.openSettings),
        Tile("Downloads", "Game, mods, modpacks", "D", actions.openDownloads),
        Tile("Crash Logs", "Logs & files", "L", actions.openFiles),
        Tile("Storage", "Game directory files", "F", actions.openFiles),
        Tile("Studio", "Customize AddaLauncher", "\u2726") { studio = true },
        Tile("Settings", "Launcher settings", "\u2699", actions.openSettings),
        Tile("About", "About AddaLauncher", "i", actions.openAbout)
    )

    Box(Modifier.fillMaxSize()) {
        AddaBackground()
        Row(Modifier.fillMaxSize().padding(18.dp), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            // Left hero panel
            Column(
                Modifier.fillMaxHeight().weight(0.36f)
                    .clip(RoundedCornerShape(24.dp)).background(Color.White.copy(alpha = 0.06f))
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)), RoundedCornerShape(24.dp))
                    .padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AddaLogo(52.dp)
                        Column(Modifier.padding(start = 12.dp)) {
                            Text("AddaLauncher", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                            Text("Minecraft Java Edition", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    // Version / profile selector
                    Box {
                        InfoChip("Profile / Version", current?.getVersionName() ?: "No version installed") { menu = true }
                        DropdownMenu(menu, { menu = false }) {
                            if (versions.isEmpty()) {
                                DropdownMenuItem({ Text("Install a version") }, { menu = false; actions.openDownloads() })
                            }
                            versions.forEach { v ->
                                DropdownMenuItem({ Text(v.getVersionName()) }, {
                                    menu = false
                                    VersionsManager.saveCurrentVersion(v.getVersionName())
                                })
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    InfoChip("Microsoft / Offline account", account?.username ?: "Not signed in", actions.openAccounts)
                }
                PlayButton(enabled = current != null) {
                    launching = true
                    actions.launch(current)
                }
            }
            // Feature grid
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                modifier = Modifier.fillMaxHeight().weight(0.64f),
                contentPadding = PaddingValues(2.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tiles) { TileCard(it) }
            }
        }
        AddaLaunchOverlay(launching, current?.getVersionName() ?: "") { launching = false }
        if (studio) AddaStudioDialog { studio = false }
    }
}


@Composable
private fun InfoChip(label: String, value: String, onClick: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color.Black.copy(alpha = 0.35f))
            .clickable(onClick = onClick).padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(label, color = AddaStudio.secondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(value, color = Color.White, fontSize = 15.sp, maxLines = 1)
    }
}

@Composable
private fun PlayButton(enabled: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    Box(
        Modifier.fillMaxWidth().height(60.dp)
            .then(if (AddaStudio.glowOn) Modifier.shadow(20.dp, shape, ambientColor = AddaStudio.primary, spotColor = AddaStudio.accent) else Modifier)
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(AddaStudio.primary, AddaStudio.accent)).let { if (enabled) it else Brush.horizontalGradient(listOf(Color.Gray, Color.DarkGray)) })
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text("\u25B6  PLAY", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
    }
}

@Composable
private fun TileCard(tile: Tile) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color.White.copy(alpha = 0.07f))
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.10f)), RoundedCornerShape(18.dp))
            .clickable(onClick = tile.onClick).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(38.dp).clip(CircleShape).background(Brush.linearGradient(listOf(AddaStudio.primary, AddaStudio.secondary))),
            contentAlignment = Alignment.Center
        ) { Text(tile.glyph, color = Color.White, fontWeight = FontWeight.Black) }
        Column(Modifier.padding(start = 10.dp)) {
            Text(tile.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(tile.subtitle, color = Color.White.copy(alpha = 0.55f), fontSize = 10.sp, maxLines = 1)
        }
    }
}
