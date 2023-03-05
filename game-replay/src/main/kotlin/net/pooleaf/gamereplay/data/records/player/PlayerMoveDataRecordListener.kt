package net.pooleaf.gamereplay.data.records.player

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.player.PlayerMoveData
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

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