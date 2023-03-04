package net.pooleaf.gamereplay.data.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import net.minecraft.server.v1_8_R3.EntityItem
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import net.pooleaf.gamereplay.replay.ReplayPlayer
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * 아이템 Entity Index 10
 * https://wiki.vg/index.php?title=Entity_metadata&oldid=7415#Entity
 */
data class ItemMetaDataData(
    var entityId: Int = 0,
    var value: ItemStack? = null
) : RecordData {

    override val type: String = "itemMetaData"

}

class ItemMetaDataDataRecordListener : PacketAdapter(GameCore.gamePlugin, PacketType.Play.Server.ENTITY_METADATA) {

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

class ItemMetaDataDataReplayHandler : RecordDataReplayHandler<ItemMetaDataData> {

    override fun onPlay(recordData: ItemMetaDataData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val location = replayPlayer.viewer.location
        val entityItem = EntityItem((location.world as CraftWorld).handle, location.x, location.y, location.z, CraftItemStack.asNMSCopy(recordData.value!!.clone()))

        val wrappedDataWatcher = WrappedDataWatcher(entityItem.dataWatcher)
        wrappedDataWatcher.setObject(10, recordData.value)

        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA)
        packet.integers.write(0, recordData.entityId + ReplayPlayer.ENTITY_ID_OFFSET)
        packet.watchableCollectionModifier.write(0, wrappedDataWatcher.watchableObjects)
        ProtocolLibrary.getProtocolManager().sendServerPacket(replayPlayer.viewer, packet)
    }

}