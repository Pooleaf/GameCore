package net.pooleaf.gamereplay.data.replays.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity
import net.pooleaf.gamereplay.data.datas.entity.SpawnEntityData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import net.pooleaf.gamereplay.replay.ReplayPlayer
import org.bukkit.entity.Player

class SpawnEntityDataReplayHandler : RecordDataReplayHandler<SpawnEntityData> {

    override fun onPlay(recordData: SpawnEntityData, viewer: Player) {
        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY)
        packet.integers.write(0, recordData.entityId + ReplayPlayer.ENTITY_ID_OFFSET)
        packet.integers.write(1, recordData.x)
        packet.integers.write(2, recordData.y)
        packet.integers.write(3, recordData.z)
        packet.integers.write(4, recordData.optionalSpeedX)
        packet.integers.write(5, recordData.optionalSpeedY)
        packet.integers.write(6, recordData.optionalSpeedZ)
        packet.integers.write(7, recordData.yaw)
        packet.integers.write(8, recordData.pitch)
        packet.integers.write(9, recordData.objectType)
        packet.integers.write(10, recordData.objectData)
        ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, packet)
    }

}