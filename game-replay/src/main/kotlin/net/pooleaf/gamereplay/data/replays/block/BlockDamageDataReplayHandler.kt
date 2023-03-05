package net.pooleaf.gamereplay.data.replays.block

import net.minecraft.server.v1_8_R3.BlockPosition
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation
import net.pooleaf.core.modules.support.bukkit.util.BukkitReflectionUtil
import net.pooleaf.gamereplay.data.datas.block.BlockDamageData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player

class BlockDamageDataReplayHandler : RecordDataReplayHandler<BlockDamageData> {

    override fun onPlay(recordData: BlockDamageData, viewer: Player) {
        val blockPosition = BlockPosition(recordData.x, recordData.y, recordData.z)
        val packet = PacketPlayOutBlockBreakAnimation(viewer.entityId, blockPosition, recordData.state)
        BukkitReflectionUtil.sendPacket(viewer, packet)
    }

}