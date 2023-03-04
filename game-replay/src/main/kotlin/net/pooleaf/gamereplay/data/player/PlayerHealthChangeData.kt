package net.pooleaf.gamereplay.data.player

import com.google.gson.annotations.Expose
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.events.RecordTickEvent
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*

data class PlayerHealthChangeData(
    var playerUuid: UUID? = null,
    var health: Double = 0.0
) : RecordData {

    override val type: String = "healthChange"

}

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

class PlayerHealthChangeDataReplayHandler : RecordDataReplayHandler<PlayerHealthChangeData> {

    override fun onPlay(recordData: PlayerHealthChangeData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val replayNpc = replayPlayer.virtualPlayerManager.get(recordData.playerUuid) ?: return
        replayNpc.health = recordData.health
    }

}