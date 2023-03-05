package net.pooleaf.gamereplay.data.replays.block

import net.pooleaf.core.modules.support.bukkit.util.BukkitReflectionUtil
import net.pooleaf.gamereplay.data.datas.block.BlockBreakData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.Location
import org.bukkit.entity.Player

class BlockBreakDataReplayHandler : RecordDataReplayHandler<BlockBreakData> {

    override fun onPlay(recordData: BlockBreakData, viewer: Player) {
        val location = Location(viewer.world, recordData.x, recordData.y, recordData.z)

        val nmsBlock = BukkitReflectionUtil.getNmsBlock(recordData.blockTypeId)
        val breakSound = BukkitReflectionUtil.getBlockPlaceSound(nmsBlock)
        val volume = (BukkitReflectionUtil.getBlockSoundVolume(nmsBlock) + 1.0F) / 2.0F
        val pitch = BukkitReflectionUtil.getBlockSoundPitch(nmsBlock) * 0.8F

        viewer.playSound(location, breakSound, volume, pitch)
    }

}