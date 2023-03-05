package net.pooleaf.gamereplay.data.records.block

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.block.UpdateSignData
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.SignChangeEvent

class UpdateSignDataRecordListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onSignChange(event: SignChangeEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val block = event.block
        val location = block.location

        val recordData = UpdateSignData().apply {
            x = location.x.toInt()
            y = location.y.toInt()
            z = location.z.toInt()
            lines = event.lines
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}