package net.pooleaf.gamereplay.data.records.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.GameReplayPlugin
import net.pooleaf.gamereplay.data.datas.entity.ItemMetaDataData
import org.bukkit.entity.Item
import org.bukkit.inventory.ItemStack

class ItemMetaDataDataRecordListener : PacketAdapter(GameReplayPlugin.instance, PacketType.Play.Server.ENTITY_METADATA) {

    override fun onPacketSending(event: PacketEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val packet = event.packet
        val entityId = packet.integers.read(0)
        val entity = packet.getEntityModifier(event.player.world).read(0)
        if (entity !is Item) return

        val entityMetaData = packet.watchableCollectionModifier.read(0)
        if (entityMetaData.isEmpty()) return

        val index = entityMetaData.get(0).index
        val value = entityMetaData.get(0).value

        if (index != 10) return

        val recordData = ItemMetaDataData().apply {
            this.entityId = entityId
            this.value = value as ItemStack
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}