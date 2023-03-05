package net.pooleaf.gamereplay.data.records.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.GameReplayPlugin
import net.pooleaf.gamereplay.data.datas.entity.EntityHeadRotationData

class EntityHeadRotationDataRecordListener : PacketAdapter(GameReplayPlugin.instance, PacketType.Play.Server.ENTITY_HEAD_ROTATION) {

    override fun onPacketSending(event: PacketEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val packet = event.packet

        val entityId = packet.integers.read(0)
        val headYaw = packet.bytes.read(0)

        val recordData = EntityHeadRotationData().apply {
            this.entityId = entityId
            this.headYaw = headYaw
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}