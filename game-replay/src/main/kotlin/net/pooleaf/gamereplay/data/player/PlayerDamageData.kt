package net.pooleaf.gamereplay.data.player

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.cryptomorin.xseries.XSound
import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.PlayerDamageEvent
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import java.util.*

data class PlayerDamageData(
    var playerUuid: UUID? = null
) : RecordData {

    override val type: String = "playerDamage"

}

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

class PlayerDamageDataReplayHandler : RecordDataReplayHandler<PlayerDamageData> {

    override fun onPlay(recordData: PlayerDamageData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val citizensNpc = replayPlayer.virtualPlayerManager.get(recordData.playerUuid)?.citizensNpc ?: return

        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ANIMATION)
        packet.integers.write(0, citizensNpc.entity.entityId)
        packet.integers.write(1, 1)
        ProtocolLibrary.getProtocolManager().sendServerPacket(replayPlayer.viewer, packet)

        XSound.ENTITY_PLAYER_HURT.play(replayPlayer.viewer, 0.8F, 1.0F)
    }

}