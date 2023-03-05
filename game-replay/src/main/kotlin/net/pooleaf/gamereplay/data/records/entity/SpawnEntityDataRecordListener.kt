package net.pooleaf.gamereplay.data.records.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.GameReplayPlugin
import net.pooleaf.gamereplay.data.datas.entity.SpawnEntityData

class SpawnEntityDataRecordListener : PacketAdapter(GameReplayPlugin.instance, PacketType.Play.Server.SPAWN_ENTITY) {

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