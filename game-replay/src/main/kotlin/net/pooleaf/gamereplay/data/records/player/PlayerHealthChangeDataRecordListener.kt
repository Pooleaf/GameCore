package net.pooleaf.gamereplay.data.records.player

import com.google.gson.annotations.Expose
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.player.PlayerHealthChangeData
import net.pooleaf.gamereplay.events.RecordTickEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*

class PlayerHealthChangeDataRecordListener : Listener {

    @Expose
    private val beforeHealths = hashMapOf<UUID, Double>()

    @EventHandler
    fun onHeathChange(event: RecordTickEvent) {
        event.record.recordTargetPlayers.forEach { uuid ->
            val player = Bukkit.getPlayer(uuid)
            if (player == null) return@forEach

            val beforeHealth = beforeHealths.getOrDefault(player.uniqueId, 0.0)
            val currentHealth = player.health

            if (beforeHealth != currentHealth) {
                val recordData = PlayerHealthChangeData().apply {
                    playerUuid = player.uniqueId
                    health = currentHealth
                }
                GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
            }

            beforeHealths.put(player.uniqueId, currentHealth)
        }
    }

}