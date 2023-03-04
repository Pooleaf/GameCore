package net.pooleaf.gamereplay.data.block

import net.pooleaf.core.modules.support.bukkit.util.BukkitReflectionUtil
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

/**
 * 블럭 파괴 사운드 재생용
 * 블럭 변경은 [BlockChangeData]에서 담당
 */
data class BlockBreakData(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var blockTypeId: Int = 0
) : RecordData {

    override val type: String = "blockBreak"

}

class BlockBreakDataRecordListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return
        if (!GameReplayApi.unsafe.recordManager.isRecordingTargetPlayer(event.player)) return

        val block = event.block
        val location = block.location

        val recordData = BlockBreakData().apply {
            x = location.x
            y = location.y
            z = location.z
            blockTypeId = block.typeId
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

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