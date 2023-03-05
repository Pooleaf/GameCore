package net.pooleaf.gamereplay.data.records.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.GameReplayPlugin
import net.pooleaf.gamereplay.data.datas.entity.EntityVelocityData

class EntityVelocityDataRecordListener : PacketAdapter(GameReplayPlugin.instance, PacketType.Play.Server.ENTITY_VELOCITY) {

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