package net.pooleaf.gamereplay.data.records.game

import net.pooleaf.gamecore.events.game.GameEndEvent
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.game.GameEndData
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class GameEndDataRecordListener : Listener {

    @EventHandler
    fun onGameEnd(event: GameEndEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val winnerPlayerUuids = event.winnerTeam?.players?.map { it.uuid } ?: arrayListOf()

        val recordData = GameEndData().apply {
            this.winnerPlayerUuidss = winnerPlayerUuids
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}