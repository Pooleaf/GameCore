package net.pooleaf.gamereplay.data.records.game

import net.pooleaf.gamecore.events.player.GamePlayerDefeatEvent
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.game.GamePlayerDefeatData
import net.pooleaf.gamereplay.data.datas.player.PlayerHideData
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class GamePlayerDefeatDataRecordListener : Listener {

    @EventHandler
    fun onGameEnd(event: GamePlayerDefeatEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val recordData = GamePlayerDefeatData().apply {
            defeatPlayerUuid = event.gamePlayer.uuid
            killerPlayerUuid = event.killerGamePlayer?.uuid
        }
        val hideData = PlayerHideData().apply {
            playerUuid = event.gamePlayer.uuid
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(hideData)
    }

}