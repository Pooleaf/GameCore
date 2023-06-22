package net.pooleaf.gamereplay.data.replays.game

import com.cryptomorin.xseries.XSound
import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.core.modules.support.bukkit.sound.playSound
import net.pooleaf.gamereplay.data.datas.game.GameEndData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player

class GameEndDataReplayHandler : RecordDataReplayHandler<GameEndData> {

    override fun onPlay(recordData: GameEndData, viewer: Player) {
        val winnerPlayerNames = recordData.winnerPlayerUuidss.map { CommonSenderModule.getOfflinePlayer(it)?.displayName ?: it.toString() }.joinToString()

        // 우승 타이틀
        viewer.sendTitle("§e우승", "§f${winnerPlayerNames}")

        // 사운드
        viewer.playSound(XSound.ENTITY_PLAYER_LEVELUP, 0.4F, 0.5F)
    }

}