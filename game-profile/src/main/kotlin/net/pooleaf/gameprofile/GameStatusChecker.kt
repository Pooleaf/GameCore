package net.pooleaf.gameprofile

import org.bukkit.Bukkit

object GameStatusChecker {

    fun isGameChannel(): Boolean = Bukkit.getPluginManager().getPlugin("GameCore") != null

    fun isGameStarted(): Boolean = net.pooleaf.gamecore.GameCore.game.isGameStarted

}