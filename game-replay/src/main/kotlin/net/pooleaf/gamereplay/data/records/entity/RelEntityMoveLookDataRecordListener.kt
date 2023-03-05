package net.pooleaf.gamereplay.data.records.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.GameReplayPlugin
import net.pooleaf.gamereplay.data.datas.entity.RelEntityMoveData
import net.pooleaf.gamereplay.data.datas.entity.RelEntityMoveLookData

class RelEntityMoveLookDataRecordListener : PacketAdapter(GameReplayPlugin.instance, PacketType.Play.Server.REL_ENTITY_MOVE_LOOK) {

    override fun onPacketSending(event: PacketEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val packet = event.packet

        val entityId = packet.integers.read(0)
        val dx = packet.bytes.read(0)
        val dy = packet.bytes.read(1)
        val dz = packet.bytes.read(2)
        val yaw = packet.bytes.read(3)
        val pitch = packet.bytes.read(4)
        val onGround = packet.getSpecificModifier(Boolean::class.java).read(0)

        val recordData = RelEntityMoveLookData().apply {
            this.entityId = entityId
            this.dx = dx
            this.dy = dy
            this.dz = dz
            this.yaw = yaw
            this.pitch = pitch
            this.onGround = onGround
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}