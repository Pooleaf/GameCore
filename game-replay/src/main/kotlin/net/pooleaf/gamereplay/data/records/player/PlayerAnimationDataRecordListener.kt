package net.pooleaf.gamereplay.data.records.player

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.player.PlayerAnimationData
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAnimationEvent

class PlayerAnimationDataRecordListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerAnimation(event: PlayerAnimationEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val player = event.player
        if (!GameReplayApi.unsafe.recordManager.isRecordingTargetPlayer(player)) return

        val recordData = PlayerAnimationData().apply {
            playerUuid = player.uniqueId
            animationType = event.animationType.name
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}