package net.pooleaf.gamereconnector

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.channel.ChannelModule
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.support.bukkit.sound.playSound
import net.pooleaf.core.modules.support.common.component.SimpleComponentBuilder
import net.pooleaf.gamehistory.GameHistoryApi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class GameReconnectListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        BukkitAsyncScope.launch {
            val recentGameId = GameHistoryApi.unsafe.sqlManager.gameDao.selectPlayingGameIdByPlayerUuid(player.uniqueId.toString()) ?: return@launch
            val recentGame = GameHistoryApi.unsafe.sqlManager.gameDao.selectGame(recentGameId) ?: return@launch

            val gameChannel = ChannelModule.getChannel(recentGame.channelName)
            if (!gameChannel.isOnline) return@launch

            delay(1500L)

            player.sendMessage("")
            player?.sendMessage("§e§l====================================================")
            player.sendMessage("")
            player.sendMessage("§e참여 중인 게임이 있습니다!")
            player.sendMessage(
                SimpleComponentBuilder("§6§l[여기]§e를 클릭하여 다시 게임에 참여하세요!")
                .clickRunCommand("/재접속")
                .hoverShowText("§e클릭 시 §f${gameChannel.displayName} §e채널로 이동합니다.")
                .build())
            player.sendMessage("")
            player?.sendMessage("§e§l====================================================")
            player.playSound(XSound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.4F, 1.0F)
        }
    }

}