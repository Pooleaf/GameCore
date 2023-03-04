package net.pooleaf.gamereplay.data.game

import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.gamecore.events.team.TeamDefeatEvent
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*

/**
 * 팀 탈락 데이터
 */
data class TeamDefeatData(
    var teamId: Int = -1,
    var teamName: String? = null,
    var teamPlayerUuids: List<UUID> = arrayListOf(),
    var killerPlayerUuid: UUID? = null
) : RecordData {

    override val type: String = "teamDefeat"

}

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

class TeamDefeatDataReplayHandler : RecordDataReplayHandler<TeamDefeatData> {

    override fun onPlay(recordData: TeamDefeatData, viewer: Player) {
        if (recordData.teamPlayerUuids.size <= 1) return

        val teamName = if (recordData.teamName == null) {
            recordData.teamPlayerUuids.map { CommonSenderModule.getPlayer(it)?.displayName ?: it.toString() }
        } else {
            recordData.teamName
        }

        viewer.sendMessage("§c${teamName} §c팀이 탈락했습니다.")
    }

}