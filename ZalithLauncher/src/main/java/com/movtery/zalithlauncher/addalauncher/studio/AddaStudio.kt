/*
 * AddaLauncher - custom launcher UI layer.
 * Built on Zalith Launcher 2 (GPLv3). Distributed under GPL v3 or later.
 */
package com.movtery.zalithlauncher.addalauncher.studio

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

enum class AddaPerfMode(val label: String, val particles: Int, val lights: Int) {
    LOW("Low-End", 0, 1),
    BALANCED("Balanced", 28, 3),
    HIGH("High Quality", 60, 4)
}

/** Persistent, observable AddaLauncher Studio settings (SharedPreferences backed). */
object AddaStudio {
    const val DEFAULT_MESSAGES = "Preparing AddaLauncher...\nPreparing Minecraft...\nLoading Java Runtime...\n" +
        "Loading libraries...\nPreparing renderer...\nLoading assets...\nInitializing client...\nStarting Minecraft..."

    private val DEF_PRIMARY = Color(0xFF7C4DFF)
    private val DEF_SECONDARY = Color(0xFF00E5FF)
    private val DEF_ACCENT = Color(0xFFFF4081)

    private var prefs: SharedPreferences? = null

    var primary by mutableStateOf(DEF_PRIMARY); private set
    var secondary by mutableStateOf(DEF_SECONDARY); private set
    var accent by mutableStateOf(DEF_ACCENT); private set
    var particlesOn by mutableStateOf(true); private set
    var glowOn by mutableStateOf(true); private set
    var movingLightsOn by mutableStateOf(true); private set
    var reducedAnimations by mutableStateOf(false); private set
    var reducedParticles by mutableStateOf(false); private set
    var musicOn by mutableStateOf(false); private set
    var perfMode by mutableStateOf(AddaPerfMode.BALANCED); private set
    var loadingMessages by mutableStateOf(DEFAULT_MESSAGES); private set

    fun init(context: Context) {
        if (prefs != null) return
        val p = context.applicationContext.getSharedPreferences("adda_studio", Context.MODE_PRIVATE)
        prefs = p
        primary = Color(p.getInt("primary", DEF_PRIMARY.toArgb()))
        secondary = Color(p.getInt("secondary", DEF_SECONDARY.toArgb()))
        accent = Color(p.getInt("accent", DEF_ACCENT.toArgb()))
        particlesOn = p.getBoolean("particles", true)
        glowOn = p.getBoolean("glow", true)
        movingLightsOn = p.getBoolean("lights", true)
        reducedAnimations = p.getBoolean("reduced_anim", false)
        reducedParticles = p.getBoolean("reduced_particles", false)
        musicOn = p.getBoolean("music", false)
        perfMode = runCatching { AddaPerfMode.valueOf(p.getString("perf", "BALANCED") ?: "BALANCED") }
            .getOrDefault(AddaPerfMode.BALANCED)
        loadingMessages = p.getString("messages", DEFAULT_MESSAGES) ?: DEFAULT_MESSAGES
    }

    private fun edit(block: SharedPreferences.Editor.() -> Unit) {
        prefs?.edit()?.also { it.block() }?.apply()
    }

    fun setPrimary(c: Color) { primary = c; edit { putInt("primary", c.toArgb()) } }
    fun setSecondary(c: Color) { secondary = c; edit { putInt("secondary", c.toArgb()) } }
    fun setAccent(c: Color) { accent = c; edit { putInt("accent", c.toArgb()) } }
    fun setParticles(v: Boolean) { particlesOn = v; edit { putBoolean("particles", v) } }
    fun setGlow(v: Boolean) { glowOn = v; edit { putBoolean("glow", v) } }
    fun setMovingLights(v: Boolean) { movingLightsOn = v; edit { putBoolean("lights", v) } }
    fun setReducedAnimations(v: Boolean) { reducedAnimations = v; edit { putBoolean("reduced_anim", v) } }
    fun setReducedParticles(v: Boolean) { reducedParticles = v; edit { putBoolean("reduced_particles", v) } }
    fun setMusic(v: Boolean) { musicOn = v; edit { putBoolean("music", v) } }
    fun setPerfMode(m: AddaPerfMode) { perfMode = m; edit { putString("perf", m.name) } }
    fun setLoadingMessages(v: String) { loadingMessages = v; edit { putString("messages", v) } }

    fun resetColors() { setPrimary(DEF_PRIMARY); setSecondary(DEF_SECONDARY); setAccent(DEF_ACCENT) }

    fun messageList(): List<String> =
        loadingMessages.lines().map { it.trim() }.filter { it.isNotEmpty() }
            .ifEmpty { DEFAULT_MESSAGES.lines() }

    /** Particle count after performance mode and the reduced-particles option. */
    fun effectiveParticles(): Int {
        if (!particlesOn) return 0
        val base = perfMode.particles
        return if (reducedParticles) base / 3 else base
    }
}
