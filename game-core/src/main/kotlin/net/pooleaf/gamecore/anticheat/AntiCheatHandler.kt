package net.pooleaf.gamecore.anticheat

import org.bukkit.entity.Player

interface AntiCheatHandler {

    fun exempt(player: Player, checkType: AntiCheatCheckType)

    fun unexempt(player: Player, checkType: AntiCheatCheckType)

}