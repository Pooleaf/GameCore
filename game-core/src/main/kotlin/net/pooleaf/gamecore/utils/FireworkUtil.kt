package net.pooleaf.gamecore.utils

import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework

object FireworkUtil {

    fun shootRandomFirework(location: Location) {
        val firework = location.world.spawnEntity(location, EntityType.FIREWORK) as Firework

        val fireworkMeta = firework.fireworkMeta
        fireworkMeta.power = 1
        fireworkMeta.addEffect(
            FireworkEffect.builder()
                .withColor(getRandomColor())
                .build()
        )

        firework.fireworkMeta = fireworkMeta
    }

    fun getRandomColor(): Color? {
        return getColor((Math.random() * 17 + 1).toInt())
    }

    fun getColor(i: Int): Color? {
        when (i) {
            1 -> return Color.WHITE
            2 -> return Color.SILVER
            3 -> return Color.GRAY
            4 -> return Color.BLACK
            5 -> return Color.RED
            6 -> return Color.MAROON
            7 -> return Color.YELLOW
            8 -> return Color.OLIVE
            9 -> return Color.LIME
            10 -> return Color.GREEN
            11 -> return Color.AQUA
            12 -> return Color.TEAL
            13 -> return Color.BLUE
            14 -> return Color.NAVY
            15 -> return Color.FUCHSIA
            16 -> return Color.PURPLE
            17 -> return Color.ORANGE
        }
        return null
    }

}