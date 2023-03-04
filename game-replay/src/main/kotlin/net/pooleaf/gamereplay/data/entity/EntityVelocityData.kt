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

data class EntityVelocityData(
    var entityId: Int = 0,
    var velocityX: Int = 0,
    var velocityY: Int = 0,
    var velocityZ: Int = 0
) : RecordData {

    override val type: String = "entityVelocity"

}

class EntityVelocityDataRecordListener : PacketAdapter(GameCore.gamePlugin, PacketType.Play.Server.ENTITY_VELOCITY) {

    override fun onPacketSending(event: PacketEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val packet = event.packet

        val packetEntityId = packet.integers.read(0)
        val packetVelocityX = packet.integers.read(1)
        val packetVelocityY = packet.integers.read(2)
        val packetVelocityZ = packet.integers.read(3)

        val recordData = EntityVelocityData().apply {
            entityId = packetEntityId
            velocityX = packetVelocityX
            velocityY = packetVelocityY
            velocityZ = packetVelocityZ
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class EntityVelocityDataReplayHandler : RecordDataReplayHandler<EntityVelocityData> {

    override fun onPlay(recordData: EntityVelocityData, viewer: Player) {
        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_VELOCITY)
        packet.integers.write(0, recordData.entityId + ReplayPlayer.ENTITY_ID_OFFSET)
        packet.integers.write(1, recordData.velocityX)
        packet.integers.write(2, recordData.velocityY)
        packet.integers.write(3, recordData.velocityZ)
        ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, packet)
    }

}