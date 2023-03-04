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

data class SpawnEntityData(
    var entityId: Int = 0,
    var x: Int = 0,
    var y: Int = 0,
    var z: Int = 0,
    var optionalSpeedX: Int = 0,
    var optionalSpeedY: Int = 0,
    var optionalSpeedZ: Int = 0,
    var yaw: Int = 0,
    var pitch: Int = 0,
    var objectType: Int = 0,
    var objectData: Int = 0
) : RecordData {

    override val type: String = "spawnEntity"

}

class SpawnEntityDataRecordListener : PacketAdapter(GameCore.gamePlugin, PacketType.Play.Server.SPAWN_ENTITY) {

    override fun onPacketSending(event: PacketEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val packet = event.packet

        val packetEntityId = packet.integers.read(0)
        val packetX = packet.integers.read(1)
        val packetY = packet.integers.read(2)
        val packetZ = packet.integers.read(3)
        val packetOptionalSpeedX = packet.integers.read(4)
        val packetOptionalSpeedY = packet.integers.read(5)
        val packetOptionalSpeedZ = packet.integers.read(6)
        val packetYaw = packet.integers.read(7)
        val packetPitch = packet.integers.read(8)
        val packetType = packet.integers.read(9)
        val packetObjectData = packet.integers.read(10)

        val recordData = SpawnEntityData().apply {
            entityId = packetEntityId
            x = packetX
            y = packetY
            z = packetZ
            optionalSpeedX = packetOptionalSpeedX
            optionalSpeedY = packetOptionalSpeedY
            optionalSpeedZ = packetOptionalSpeedZ
            yaw = packetYaw
            pitch = packetPitch
            objectType = packetType
            objectData = packetObjectData
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class SpawnEntityDataReplayHandler : RecordDataReplayHandler<SpawnEntityData> {

    override fun onPlay(recordData: SpawnEntityData, viewer: Player) {
        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY)
        packet.integers.write(0, recordData.entityId + ReplayPlayer.ENTITY_ID_OFFSET)
        packet.integers.write(1, recordData.x)
        packet.integers.write(2, recordData.y)
        packet.integers.write(3, recordData.z)
        packet.integers.write(4, recordData.optionalSpeedX)
        packet.integers.write(5, recordData.optionalSpeedY)
        packet.integers.write(6, recordData.optionalSpeedZ)
        packet.integers.write(7, recordData.yaw)
        packet.integers.write(8, recordData.pitch)
        packet.integers.write(9, recordData.objectType)
        packet.integers.write(10, recordData.objectData)
        ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, packet)
    }

}