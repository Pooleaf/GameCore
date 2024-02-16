package net.pooleaf.gamecore.utils

import net.pooleaf.gamecore.GameCore
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

fun LivingEntity.damageBypassAntiCheat(damage: Double, damagedBy: Player) {
    GameCore.unsafe.antiCheatBypassService.damage(this, damage, damagedBy)
}