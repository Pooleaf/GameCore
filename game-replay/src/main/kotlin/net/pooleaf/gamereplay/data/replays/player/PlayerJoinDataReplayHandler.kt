package net.pooleaf.gamereplay.data.replays.player

import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.player.PlayerJoinData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player

class PlayerJoinDataReplayHandler : RecordDataReplayHandler<PlayerJoinData> {

    override fun onPlay(recordData: PlayerJoinData, viewer: Player) {
        val joinCommonPlayer = CommonSenderModule.getPlayer(recordData.playerUuid)
        val joinPlayerName = joinCommonPlayer?.displayName ?: recordData.playerUuid
        viewer.sendMessage("§7[리플레이] §f${joinPlayerName} §e님께서 접속했습니다.")

        // 온라인 처리
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)
        val virtualPlayer = replayPlayer.virtualPlayerManager.get(recordData.playerUuid)
        virtualPlayer.isOnline = true

        // 텔레포터 GUI 업데이트
        replayPlayer.quickBar.replayTeleporterGui.updateAsynchronously()
    }

}