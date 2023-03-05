package net.pooleaf.gamereplay.replay

import net.pooleaf.gamereplay.data.RecordData
import org.bukkit.Bukkit
import org.bukkit.Location
import java.time.LocalDateTime
import java.util.*

data class Replay(
    val gameId: UUID,
    var recordedPlayers: List<UUID>,
    val worldName: String,
    val x: Double,
    val y: Double,
    val z: Double
) {

    var createdAt: LocalDateTime? = null
    var endTick: Long = 0L

    var recordDatas: HashMap<Long, LinkedList<RecordData>> = hashMapOf()

    val startLocation
        get() = Location(Bukkit.getWorld(worldName), x, y, z)

}