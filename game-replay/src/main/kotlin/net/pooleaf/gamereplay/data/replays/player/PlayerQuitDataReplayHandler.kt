package net.pooleaf.gamereplay.data.replays.player

import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.gamereplay.data.datas.player.PlayerQuitData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player

class PlayerQuitDataReplayHandler : RecordDataReplayHandler<PlayerQuitData> {

    override fun onPlay(recordData: PlayerQuitData, viewer: Player) {
        val quitCommonPlayer = CommonSenderModule.getPlayer(recordData.playerUuid)
        val quitPlayerName = quitCommonPlayer?.displayName ?: recordData.playerUuid
        viewer.sendMessage("§7[리플레이] §f${quitPlayerName} §e님이 퇴장했습니다.")
    }

}