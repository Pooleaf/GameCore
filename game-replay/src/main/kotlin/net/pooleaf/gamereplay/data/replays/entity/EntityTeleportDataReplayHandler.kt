package net.pooleaf.gamereplay.data.replays.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import net.pooleaf.gamereplay.data.datas.entity.EntityTeleportData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import net.pooleaf.gamereplay.replay.ReplayPlayer
import org.bukkit.entity.Player

class EntityTeleportDataReplayHandler : RecordDataReplayHandler<EntityTeleportData> {

    override fun onPlay(recordData: EntityTeleportData, viewer: Player) {
        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_TELEPORT)
        packet.integers.write(0, recordData.entityId + ReplayPlayer.ENTITY_ID_OFFSET)
        packet.integers.write(1, recordData.x)
        packet.integers.write(2, recordData.y)
        packet.integers.write(3, recordData.z)
        packet.bytes.write(0, recordData.yaw)
        packet.bytes.write(1, recordData.pitch)
        ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, packet)
    }

}