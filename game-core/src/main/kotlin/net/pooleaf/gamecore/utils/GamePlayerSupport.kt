package net.pooleaf.gamecore.utils

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.player.GamePlayer
import org.bukkit.entity.Player

fun Player.toGamePlayer(): GamePlayer? {
    return GameCore.unsafe.playerManager.get(this.uniqueId)
}