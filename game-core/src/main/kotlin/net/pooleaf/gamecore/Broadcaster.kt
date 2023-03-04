package net.pooleaf.gamecore

import com.cryptomorin.xseries.XSound
import net.md_5.bungee.api.chat.BaseComponent
import net.pooleaf.core.modules.gui.bukkit.actionbar.removeActionBar
import net.pooleaf.core.modules.gui.bukkit.actionbar.showActionBar
import net.pooleaf.core.modules.gui.bukkit.actionbar.showActionBarForever
import net.pooleaf.core.modules.gui.bukkit.title.DefaultTitleBuilder
import net.pooleaf.core.modules.gui.bukkit.title.Title
import net.pooleaf.core.modules.support.bukkit.sound.playSound
import org.bukkit.Bukkit

object Broadcaster {

    /**
     * Chat
     */

    fun broadcast(message: String) {
        Bukkit.broadcastMessage(message)
    }

    fun broadcast(component: BaseComponent) {
        Bukkit.broadcast(component)
    }

    fun broadcastWarning(message: String) {
        broadcast("§c${message}")
    }

    fun broadcastTitle(title: Title) {
        Bukkit.getOnlinePlayers().forEach { title.send(it) }
    }

    fun broadcastTitle(
        title: String,
        subtitle: String? = null,
        stayTick: Int? = null,
        fadeInTick: Int? = null,
        fadeOutTick: Int? = null
    ) {
        val titleBuilder = DefaultTitleBuilder()
            .title(title)
            .subtitle(subtitle)

        stayTick?.let {
            titleBuilder.stay(stayTick)
        }

        fadeInTick?.let {
            titleBuilder.fadeIn(fadeInTick)
        }

        fadeOutTick?.let {
            titleBuilder.fadeOut(fadeOutTick)
        }

        broadcastTitle(titleBuilder.build())
    }

    /**
     * ActionBar
     */

    fun broadcastActionBar(message: String) {
        Bukkit.getOnlinePlayers().forEach { it.showActionBar(message) }
    }

    fun broadcastActionBar(message: String, seconds: Int) {
        Bukkit.getOnlinePlayers().forEach { it.showActionBar(message, seconds) }
    }

    fun broadcastActionBarForever(message: String) {
        Bukkit.getOnlinePlayers().forEach { it.showActionBarForever(message) }
    }

    fun broadcastWaitingActionBar(currentJoinedCount: Int, startPlayerCount: Int, ) {
        broadcastActionBarForever("§e다른 플레이어를 기다리는 중입니다. §f($currentJoinedCount/$startPlayerCount)")
    }

    fun removeActionBar() {
        Bukkit.getOnlinePlayers().forEach { it.removeActionBar() }
    }

    /**
     * Sound
     */

    fun broadcastSound(sound: XSound, volume: Float = 1.0F, pitch: Float = 1.0F) {
        Bukkit.getOnlinePlayers().forEach { it.playSound(sound, volume, pitch) }
    }

}