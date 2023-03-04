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
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.*

class PlayerMoveData(
    var playerUuid: UUID? = null,
    var worldName: String? = null,
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var yaw: Float = 0.0F,
    var pitch: Float = 0.0F
) : RecordData {

    override val type: String = "playerMove"

}

class PlayerMoveDataRecordListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val player = event.player
        if (!GameReplayApi.unsafe.recordManager.isRecordingTargetPlayer(player)) return

        val location = event.to

        val recordData = PlayerMoveData().apply {
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

class PlayerMoveDataReplayHandler : RecordDataReplayHandler<PlayerMoveData> {

    override fun onPlay(recordData: PlayerMoveData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)
        val citizensNpc = replayPlayer.virtualPlayerManager.get(recordData.playerUuid)?.citizensNpc ?: return

        val location = Location(Bukkit.getWorld(recordData.worldName), recordData.x, recordData.y, recordData.z, recordData.yaw, recordData.pitch)
        citizensNpc.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN)
    }

}