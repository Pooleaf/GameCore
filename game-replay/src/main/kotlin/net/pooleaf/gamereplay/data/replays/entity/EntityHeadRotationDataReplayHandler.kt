package net.pooleaf.gamereplay.data.replays.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.entity.EntityHeadRotationData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import net.pooleaf.gamereplay.replay.ReplayPlayer
import org.bukkit.entity.Player

class EntityHeadRotationDataReplayHandler : RecordDataReplayHandler<EntityHeadRotationData> {

    override fun onPlay(recordData: EntityHeadRotationData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION)
        packet.integers.write(0, recordData.entityId + ReplayPlayer.ENTITY_ID_OFFSET)
        packet.bytes.write(0, recordData.headYaw)
        ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, packet)
    }

}