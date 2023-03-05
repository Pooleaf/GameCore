package net.pooleaf.gamereplay.data.records.player

import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.PlayerDamageEvent
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.player.PlayerDamageData
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class PlayerDamageDataRecordListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onDamage(event: PlayerDamageEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val player = event.player
        if (!GameReplayApi.unsafe.recordManager.isRecordingTargetPlayer(player)) return

        val damageRecordData = PlayerDamageData().apply {
            playerUuid = player.uniqueId
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(damageRecordData)
    }

}