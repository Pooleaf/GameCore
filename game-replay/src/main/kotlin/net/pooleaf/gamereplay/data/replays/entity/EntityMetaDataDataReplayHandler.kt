package net.pooleaf.gamereplay.data.replays.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.wrappers.WrappedWatchableObject
import net.pooleaf.gamereplay.data.datas.entity.EntityMetaDataData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import net.pooleaf.gamereplay.replay.ReplayPlayer
import org.bukkit.entity.Player

class EntityMetaDataDataReplayHandler : RecordDataReplayHandler<EntityMetaDataData> {

    override fun onPlay(recordData: EntityMetaDataData, viewer: Player) {
        val watchableObjects = recordData.dataWatchables.map {
            var value = it.value

            // 값 타입 변환
            value = if (it.index == 1) {
                (value as Number).toShort()
            } else if (value is Double) {
                value.toFloat()
            } else if (value is Number) {
                value.toByte()
            } else {
                value
            }

            WrappedWatchableObject(it.index, value)
        }

        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA)
        packet.integers.write(0, recordData.entityId * ReplayPlayer.ENTITY_ID_OFFSET)
        packet.watchableCollectionModifier.write(0, watchableObjects)
    }

}