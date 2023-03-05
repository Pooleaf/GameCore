package net.pooleaf.gamereplay.data.records.block

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.block.BlockPlaceData
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class BlockPlaceDataRecordListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return
        if (!GameReplayApi.unsafe.recordManager.isRecordingTargetPlayer(event.player)) return

        val block = event.block
        val location = block.location

        val recordData = BlockPlaceData().apply {
            x = location.x
            y = location.y
            z = location.z
            blockTypeId = block.typeId
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}