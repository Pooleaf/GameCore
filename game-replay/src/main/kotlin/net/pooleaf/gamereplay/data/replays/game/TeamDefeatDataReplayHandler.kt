package net.pooleaf.gamereplay.data.replays.game

import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.gamereplay.data.datas.game.TeamDefeatData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player

class TeamDefeatDataReplayHandler : RecordDataReplayHandler<TeamDefeatData> {

    override fun onPlay(recordData: TeamDefeatData, viewer: Player) {
        if (recordData.teamPlayerUuids.size <= 1) return

        val teamName = if (recordData.teamName == null) {
            recordData.teamPlayerUuids.map { CommonSenderModule.getOfflinePlayer(it)?.displayName ?: it.toString() }
        } else {
            recordData.teamName
        }

        viewer.sendMessage("§c${teamName} §c팀이 탈락했습니다.")
    }

}