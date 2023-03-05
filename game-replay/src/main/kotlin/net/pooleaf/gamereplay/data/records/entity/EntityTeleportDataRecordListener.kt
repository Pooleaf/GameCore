package net.pooleaf.gamereplay.data.records.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.GameReplayPlugin
import net.pooleaf.gamereplay.data.datas.entity.EntityTeleportData

class EntityTeleportDataRecordListener : PacketAdapter(GameReplayPlugin.instance, PacketType.Play.Server.ENTITY_TELEPORT) {

    override fun onPacketSending(event: PacketEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val packet = event.packet

        val packetEntityId = packet.integers.read(0)
        val packetX = packet.integers.read(1)
        val packetY = packet.integers.read(2)
        val packetZ = packet.integers.read(3)
        val packetYaw = packet.bytes.read(0)
        val packetPitch = packet.bytes.read(1)

        val recordData = EntityTeleportData().apply {
            entityId = packetEntityId
            x = packetX
            y = packetY
            z = packetZ
            yaw = packetYaw
            pitch = packetPitch
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}