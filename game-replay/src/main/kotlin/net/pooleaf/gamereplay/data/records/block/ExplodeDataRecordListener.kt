package net.pooleaf.gamereplay.data.records.block

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.block.BlockExplodeInfo
import net.pooleaf.gamereplay.data.datas.block.ExplodeData
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityExplodeEvent

class ExplodeDataRecordListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityExplode(event: EntityExplodeEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val blockInfos = event.blockList().map { block ->
            BlockExplodeInfo().apply {
                x = block.x
                y = block.y
                z = block.z
            }
        }

        val recordData = ExplodeData().apply {
            x = event.entity.location.x
            y = event.entity.location.y
            z = event.entity.location.z
            yield = event.yield
            this.blockInfos = blockInfos
        }

        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

    @EventHandler
    fun onBlockExplode(event: BlockExplodeEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val blockInfos = event.blockList().map { block ->
            BlockExplodeInfo().apply {
                x = block.x
                y = block.y
                z = block.z
            }
        }

        val recordData = ExplodeData().apply {
            x = event.block.location.x
            y = event.block.location.y
            z = event.block.location.z
            yield = event.yield
            this.blockInfos = blockInfos
        }

        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}