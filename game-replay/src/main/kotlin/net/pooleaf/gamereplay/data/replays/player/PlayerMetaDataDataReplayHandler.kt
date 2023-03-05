package net.pooleaf.gamereplay.data.replays.player

import net.minecraft.server.v1_8_R3.EntityPlayer
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata
import net.pooleaf.core.modules.support.bukkit.util.BukkitReflectionUtil
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.player.PlayerMetaDataData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player

class PlayerMetaDataDataReplayHandler : RecordDataReplayHandler<PlayerMetaDataData> {

    override fun onPlay(recordData: PlayerMetaDataData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val citizensNpc = replayPlayer.virtualPlayerManager.get(recordData.playerUuid)?.citizensNpc ?: return

        // 불 처리
        when (recordData.value % 2) {
            0 -> citizensNpc.entity.fireTicks = 0
            1 -> citizensNpc.entity.fireTicks = 9999999
        }

        val entityPlayer = BukkitReflectionUtil.getHandle(citizensNpc.entity) as EntityPlayer
        val dataWatcher = entityPlayer.dataWatcher
        dataWatcher.watch(recordData.index, recordData.value)

        val packet = PacketPlayOutEntityMetadata(citizensNpc.entity.entityId, dataWatcher, false)
        BukkitReflectionUtil.sendPacket(replayPlayer.viewer, packet)
    }

}