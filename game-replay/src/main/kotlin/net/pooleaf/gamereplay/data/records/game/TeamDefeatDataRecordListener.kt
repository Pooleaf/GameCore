package net.pooleaf.gamereplay.data.records.game

import net.pooleaf.gamecore.events.team.TeamDefeatEvent
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.game.TeamDefeatData
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class TeamDefeatDataRecordListener : Listener {

    @EventHandler
    fun onGameEnd(event: TeamDefeatEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val defeatTeamPlayerUuids = event.team.players.map { it.uuid }

        val recordData = TeamDefeatData().apply {
            teamId = event.team.id
            teamName = event.team.teamName
            this.teamPlayerUuids = defeatTeamPlayerUuids
            killerPlayerUuid = event.killerGamePlayer?.uuid
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}