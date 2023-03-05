package net.pooleaf.gamereplay.data.replays.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import net.pooleaf.gamereplay.data.datas.entity.EntityVelocityData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import net.pooleaf.gamereplay.replay.ReplayPlayer
import org.bukkit.entity.Player

class EntityVelocityDataReplayHandler : RecordDataReplayHandler<EntityVelocityData> {

    override fun onPlay(recordData: EntityVelocityData, viewer: Player) {
        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_VELOCITY)
        packet.integers.write(0, recordData.entityId + ReplayPlayer.ENTITY_ID_OFFSET)
        packet.integers.write(1, recordData.velocityX)
        packet.integers.write(2, recordData.velocityY)
        packet.integers.write(3, recordData.velocityZ)
        ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, packet)
    }

}