package net.pooleaf.gamereplay.data.records.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.GameReplayPlugin
import net.pooleaf.gamereplay.data.datas.entity.EntityDestroyData

class EntityDestroyDataRecordListener : PacketAdapter(GameReplayPlugin.instance, PacketType.Play.Server.ENTITY_DESTROY) {

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