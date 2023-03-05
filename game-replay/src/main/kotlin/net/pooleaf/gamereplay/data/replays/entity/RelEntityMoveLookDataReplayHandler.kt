package net.pooleaf.gamereplay.data.replays.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.entity.RelEntityMoveLookData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import net.pooleaf.gamereplay.replay.ReplayPlayer
import org.bukkit.entity.Player

class RelEntityMoveLookDataReplayHandler : RecordDataReplayHandler<RelEntityMoveLookData> {

    override fun onPlay(recordData: RelEntityMoveLookData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK)
        packet.integers.write(0, recordData.entityId + ReplayPlayer.ENTITY_ID_OFFSET)
        packet.bytes.write(0, recordData.dx)
        packet.bytes.write(1, recordData.dy)
        packet.bytes.write(2, recordData.dz)
        packet.bytes.write(3, recordData.yaw)
        packet.bytes.write(4, recordData.pitch)
        packet.getSpecificModifier(Boolean::class.java).write(0, recordData.onGround)
        ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, packet)
    }

}