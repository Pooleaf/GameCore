package net.pooleaf.gamereplay.data.player

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.*

data class PlayerTeleportData(
    var playerUuid: UUID? = null,
    var worldName: String? = null,
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var yaw: Float = 0.0F,
    var pitch: Float = 0.0F
) : RecordData {

    override val type: String = "playerTeleport"

}

class PlayerTeleportDataRecordListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val player = event.player
        if (!GameReplayApi.unsafe.recordManager.isRecordingTargetPlayer(player)) return

        val location = event.to

        val recordData = PlayerTeleportData().apply {
            playerUuid = player.uniqueId
            worldName = location.world.name
            x = location.x
            y = location.y
            z = location.z
            yaw = location.yaw
            pitch = location.pitch
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class PlayerTeleportDataReplayHandler : RecordDataReplayHandler<PlayerTeleportData> {

    override fun onPlay(recordData: PlayerTeleportData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)
        val citizensNpc = replayPlayer.virtualPlayerManager.get(recordData.playerUuid)?.citizensNpc ?: return

        val location = Location(Bukkit.getWorld(recordData.worldName), recordData.x, recordData.y, recordData.z, recordData.yaw, recordData.pitch)
        citizensNpc.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN)
    }

}