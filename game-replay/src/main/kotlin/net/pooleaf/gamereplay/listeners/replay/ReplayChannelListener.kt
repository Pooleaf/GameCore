package net.pooleaf.gamereplay.listeners.replay

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.channel.ChannelModule
import net.pooleaf.core.modules.channel.common.events.ChannelMessageEvent
import net.pooleaf.core.modules.commonevent.common.CommonEventHandler
import net.pooleaf.core.modules.commonevent.common.CommonEventListener
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.support.bukkit.messager.sendWarning
import net.pooleaf.core.modules.support.bukkit.sound.playSound
import net.pooleaf.core.modules.support.common.manager.AbstractEhcacheManager
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.GameReplayPlugin
import net.pooleaf.gamereplay.channel.ReplayChannelTask
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.util.*

class ReplayChannelListener : CommonEventListener, Listener {

    companion object {
        val replayReserveManager = ReplayReserveManager()
    }


    @CommonEventHandler
    fun onMessage(event: ChannelMessageEvent) {
        when (event.task) {
            ReplayChannelTask.PLAY_REPLAY.name -> {
                val playerUuid = UUID.fromString(event.datas.get(0).toString())
                val gameId = UUID.fromString(event.datas.get(1).toString())
                val tick = event.datas.get(2).toString().toLong()

                val player = Bukkit.getPlayer(playerUuid)
                // 오프라인일 경우 예약
                if (player == null) {
                    val replayReserve = ReplayReserve(gameId, tick)

                    replayReserveManager.set(playerUuid, replayReserve)
                    replayReserveManager.setTimeToLive(playerUuid, 10)

                }
                // 리플레이 재생
                else {
                    BukkitSyncScope.launch {
                        playReplay(player, gameId, tick)
                    }
                }
            }
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        Bukkit.getScheduler().runTaskLater(GameReplayPlugin.instance, {
            val replayReserve = replayReserveManager.get(player.uniqueId)
            if (replayReserve == null) {
                // 리플레이 서버일 때 리플레이 재생하지 않을 경우 로비로 이동
                if (GameReplayApi.replayConfig.isReplayPlayServer && !GameReplayApi.unsafe.replayPlayerManager.exists(player.uniqueId)) {
                    ChannelModule.getLobbyChannelGroup().fastJoin(player.uniqueId)
                }

                return@runTaskLater
            }

            val gameId = replayReserve.gameId
            val tick = replayReserve.tick

            playReplay(player, gameId, tick)
            replayReserveManager.remove(player.uniqueId)
        }, 1L)
    }

    fun playReplay(player: Player, gameId: UUID, tick: Long) {
        try {
            GameReplayApi.unsafe.replayService.playReplay(player, gameId, tick)
        } catch (exception: Exception) {
            // 뷰어 로비로 이동
            if (GameReplayApi.replayConfig.isReplayPlayServer) {
                ChannelModule.getLobbyChannelGroup().fastJoin(player.uniqueId)
            }

            player.sendWarning("오류가 발생하여 리플레이를 재생할 수 없습니다.")
            player.playSound(XSound.BLOCK_NOTE_BLOCK_BASS)

            exception.printStackTrace()
        }
    }


    data class ReplayReserve(
        val gameId: UUID,
        val tick: Long
    ) {
    }

    class ReplayReserveManager : AbstractEhcacheManager<UUID, ReplayReserve>() { // playerUuid, replayReserve
    }

}
