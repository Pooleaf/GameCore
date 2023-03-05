package net.pooleaf.gamereplay.data.records.game

import net.pooleaf.gamecore.events.game.GameWorldBorderChangeEvent
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.game.GameWorldBorderChangeData
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class GameWorldBorderChangeDataRecordListener : Listener {

    @EventHandler
    fun onGameWorldBorderChange(event: GameWorldBorderChangeEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val recordData = GameWorldBorderChangeData().apply {
            tick = GameReplayApi.unsafe.recordManager.record!!.currentTick.toLong()
            centerX = event.centerLocation.x.toInt()
            centerZ = event.centerLocation.z.toInt()
            beforeSize = event.beforeSize
            newSize = event.newSize
            updateDurationSeconds = event.updateDurationSeconds
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}