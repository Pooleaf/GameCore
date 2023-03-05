package net.pooleaf.gamereplay.data.records.entity

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.entity.ItemDespawnData
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.ItemDespawnEvent

class ItemDespawnDataRecordListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onItemDespawn(event: ItemDespawnEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val recordData = ItemDespawnData().apply {
            entityId = event.entity.entityId
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}