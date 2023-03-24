package net.pooleaf.gamereconnector

import net.pooleaf.core.modules.annocommand.common.Command
import net.pooleaf.core.modules.annocommand.common.CommandResult
import net.pooleaf.core.modules.channel.ChannelModule
import net.pooleaf.core.modules.support.bukkit.messager.sendWarning
import net.pooleaf.gamehistory.GameHistoryApi
import org.bukkit.entity.Player

class GameReconnectCommand {

    @Command(
        name = ["재접속", "reconnect"],
        description = "참여 중인 게임에 재접속합니다."
    )
    fun reconnect(player: Player, result: CommandResult) {
        val recentGameId = GameHistoryApi.unsafe.sqlManager.gameDao.selectPlayingGameIdByPlayerUuid(player.uniqueId.toString())
        val recentGame = recentGameId?.let { GameHistoryApi.unsafe.sqlManager.gameDao.selectGame(it) }
        if (recentGame == null) {
            player.sendWarning("참여 중인 게임이 없습니다.")
            return
        }

        val channelName = recentGame.channelName
        player.sendMessage("${channelName}§e채널에 재접속 합니다.")
        ChannelModule.getChannel(channelName).join(player.uniqueId)
    }

}