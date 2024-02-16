package net.pooleaf.gamecore.anticheat

import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class AntiCheatBypassService {

    val useNcp: Boolean
        get() = Bukkit.getPluginManager().getPlugin("NoCheatPlus")?.isEnabled == true

    var antiCheatHandler: AntiCheatHandler? = null


    fun init() {
        if (useNcp) antiCheatHandler = NcpHandler()
    }

    fun damage(livingEntity: LivingEntity, damage: Double, damagedBy: Player) {
        antiCheatHandler?.exempt(damagedBy, AntiCheatCheckType.FIGHT)
        livingEntity.damage(damage, damagedBy)
        antiCheatHandler?.unexempt(damagedBy, AntiCheatCheckType.FIGHT)
    }

}