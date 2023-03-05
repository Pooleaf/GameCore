package net.pooleaf.gamereplay.data.replays.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.entity.EntityDestroyData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import net.pooleaf.gamereplay.replay.ReplayPlayer
import org.bukkit.entity.Player

class EntityDestroyDataReplayHandler : RecordDataReplayHandler<EntityDestroyData> {

    override fun onPlay(recordData: EntityDestroyData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY)
        packet.integerArrays.write(0, recordData.entityIds.map { it + ReplayPlayer.ENTITY_ID_OFFSET }.toIntArray())
        ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, packet)
    }

}