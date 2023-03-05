package net.pooleaf.gamereplay.data.replays.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import net.minecraft.server.v1_8_R3.EntityItem
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.entity.ItemMetaDataData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import net.pooleaf.gamereplay.replay.ReplayPlayer
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.entity.Player

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