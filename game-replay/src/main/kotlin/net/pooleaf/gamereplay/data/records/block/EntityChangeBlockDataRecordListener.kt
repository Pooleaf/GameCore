package net.pooleaf.gamereplay.data.records.block

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.block.EntityChangeBlockData
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityChangeBlockEvent

class EntityChangeBlockDataRecordListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityChangeBlock(event: EntityChangeBlockEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val recordData = EntityChangeBlockData().apply {
            x = event.block.x
            y = event.block.y
            z = event.block.z
            blockTypeId = event.to.id
            blockData = event.data
        }

        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}