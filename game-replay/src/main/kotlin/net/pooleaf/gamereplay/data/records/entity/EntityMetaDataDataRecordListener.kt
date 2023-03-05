package net.pooleaf.gamereplay.data.records.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.GameReplayPlugin
import net.pooleaf.gamereplay.data.datas.entity.DataWatchable
import net.pooleaf.gamereplay.data.datas.entity.EntityMetaDataData
import org.bukkit.entity.EntityType

class EntityMetaDataDataRecordListener : PacketAdapter(GameReplayPlugin.instance, PacketType.Play.Server.ENTITY_METADATA) {

    override fun onPacketSending(event: PacketEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return
        if (!GameReplayApi.unsafe.recordManager.isRecordingTargetPlayer(event.player)) return

        val packet = event.packet
        val entityId = packet.integers.read(0)
        val entity = packet.getEntityModifier(event.player.world).read(0)

        // 플레이어 제외
        if (entity.type == EntityType.PLAYER) return

        val entityMetaData = packet.watchableCollectionModifier.read(0)
        if (entityMetaData.isEmpty()) return

        val recordData = EntityMetaDataData().apply {
            this.entityId = entityId
            this.dataWatchables = entityMetaData.map { DataWatchable(it.index, it.value) }
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}