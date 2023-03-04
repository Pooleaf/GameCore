package net.pooleaf.gamereplay.data.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import net.pooleaf.gamereplay.replay.ReplayPlayer
import org.bukkit.entity.Player

data class EntityDestroyData(
    var entityIds: Array<Int> = arrayOf()
) : RecordData {

    override val type: String = "entityDestroy"


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EntityDestroyData

        if (!entityIds.contentEquals(other.entityIds)) return false

        return true
    }

    override fun hashCode(): Int {
        return entityIds.contentHashCode()
    }

}

class EntityDestroyDataRecordListener : PacketAdapter(GameCore.gamePlugin, PacketType.Play.Server.ENTITY_DESTROY) {

    override fun onPacketSending(event: PacketEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val packet = event.packet

        val packetEntityIds = packet.integerArrays.read(0)

        val recordData = EntityDestroyData().apply {
            entityIds = packetEntityIds.toTypedArray()
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class EntityDestroyDataReplayHandler : RecordDataReplayHandler<EntityDestroyData> {

    override fun onPlay(recordData: EntityDestroyData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY)
        packet.integerArrays.write(0, recordData.entityIds.map { it + ReplayPlayer.ENTITY_ID_OFFSET }.toIntArray())
        ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, packet)
    }

}