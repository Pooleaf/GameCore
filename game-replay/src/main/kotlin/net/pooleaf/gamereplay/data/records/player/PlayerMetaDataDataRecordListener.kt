package net.pooleaf.gamereplay.data.records.player

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.GameReplayPlugin
import net.pooleaf.gamereplay.data.datas.player.PlayerMetaDataData

class PlayerMetaDataDataRecordListener : PacketAdapter(GameReplayPlugin.instance, PacketType.Play.Server.ENTITY_METADATA) {

    override fun onPacketSending(event: PacketEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return
        if (!GameReplayApi.unsafe.recordManager.isRecordingTargetPlayer(event.player)) return

        val packet = event.packet
        val entityId = packet.integers.read(0)
        val entity = packet.getEntityModifier(event.player.world).read(0)

        // 본인 것만 녹화
        if (entityId != entity.entityId || entityId != event.player.entityId) return

        val entityMetaData = packet.watchableCollectionModifier.read(0)
        if (entityMetaData.isEmpty()) return

        val packetIndex = entityMetaData.get(0).index
        val packetValue = entityMetaData.get(0).value

        if (packetIndex != 0) return

        val recordData = PlayerMetaDataData().apply {
            playerUuid = event.player.uniqueId
            index = packetIndex
            value = packetValue as Byte
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}