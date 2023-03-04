package net.pooleaf.gamereplay.replay.virtual

import com.comphenix.protocol.wrappers.ChunkCoordIntPair
import org.bukkit.Location
import org.bukkit.World

data class VirtualLocation(
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float = 0.0F,
    val pitch: Float = 0.0F
) {

    fun toBukkitLocation(world: World): Location {
        return Location(world, x, y, z, yaw, pitch)
    }

    fun getChunk(): ChunkCoordIntPair {
        return ChunkCoordIntPair(x.toInt() shr 4, z.toInt() shr 4)
    }

}