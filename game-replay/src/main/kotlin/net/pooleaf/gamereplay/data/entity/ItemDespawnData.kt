package net.pooleaf.gamereplay.data.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import net.pooleaf.gamereplay.replay.ReplayPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.ItemDespawnEvent

data class ItemDespawnData(
    var entityId: Int = 0
) : RecordData {

    override val type: String = "itemDespawn"

}

class ItemDespawnDataRecordListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onItemDespawn(event: ItemDespawnEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val recordData = ItemDespawnData().apply {
            entityId = event.entity.entityId
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class ItemDespawnDataReplayHandler : RecordDataReplayHandler<ItemDespawnData> {

    override fun onPlay(recordData: ItemDespawnData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY)
        packet.integerArrays.write(0, arrayOf(recordData.entityId + ReplayPlayer.ENTITY_ID_OFFSET).toIntArray())
        ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, packet)
    }

}