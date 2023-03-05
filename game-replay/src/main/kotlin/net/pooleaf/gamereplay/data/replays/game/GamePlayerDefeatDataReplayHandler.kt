package net.pooleaf.gamereplay.data.replays.game

import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.gamereplay.data.datas.game.GamePlayerDefeatData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player

class GamePlayerDefeatDataReplayHandler : RecordDataReplayHandler<GamePlayerDefeatData> {

    override fun onPlay(recordData: GamePlayerDefeatData, viewer: Player) {
        val playerName = CommonSenderModule.getPlayer(recordData.defeatPlayerUuid)?.displayName ?: recordData.defeatPlayerUuid

        if (recordData.killerPlayerUuid == null) {
            viewer.sendMessage("§c${playerName} §c님이 탈락했습니다.")
        } else {
            val killerPlayerName = CommonSenderModule.getPlayer(recordData.killerPlayerUuid)?.displayName ?: recordData.killerPlayerUuid
            viewer.sendMessage("§c${killerPlayerName} §c님이 §c${playerName} §c님을 탈락시켰습니다.")
        }
    }

}