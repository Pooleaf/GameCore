package net.pooleaf.gamereplay.data.replays.player

import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.gamereplay.data.datas.player.PlayerChatData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player

class PlayerChatDataReplayHandler : RecordDataReplayHandler<PlayerChatData> {

    override fun onPlay(recordData: PlayerChatData, viewer: Player) {
        if (!viewer.isOp) return

        val chatCommonPlayer = CommonSenderModule.getOfflinePlayer(recordData.playerUuid)
        val chatPlayerName = chatCommonPlayer?.displayName ?: recordData.playerUuid
        viewer.sendMessage("§7[리플레이] §f${chatPlayerName}§f: ${recordData.message}")
    }

}