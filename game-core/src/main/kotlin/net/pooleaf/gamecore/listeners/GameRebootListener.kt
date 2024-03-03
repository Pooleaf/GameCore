package net.pooleaf.gamecore.listeners

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.channel.ChannelModule
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.game.GameResetEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent

class GameRebootListener : Listener {

    /**
     * 서버 재부팅이 예약되어 있을 경우
     * 게임 종료 및 리셋이 완료된 뒤 재부팅합니다.
     */
    @EventHandler
    fun onReset(event: GameResetEvent) {
        if (!GameCore.unsafe.rebootManager.rebooting) {
            // 게임 종료 시 재부팅
            if (GameCore.unsafe.autoRebootConfig.useAutoRebootOnGameEnd) {
                BukkitAsyncScope.launch {
                    // 현재 채널 그룹으로 이동
                    if (GameCore.unsafe.autoRebootConfig.useAutoRebootOnGameEndSendToCurrentGroup) {
                        Bukkit.getOnlinePlayers().forEach { ChannelModule.getCurrentChannel()?.group?.fastJoin(it.uniqueId) }
                        delay(3000L)
                        GameCore.unsafe.rebootManager.reboot()
                    }
                    // 로비 그룹 이동
                    else if (GameCore.unsafe.autoRebootConfig.useAutoRebootOnGameEndSendToLobbyGroup) {
                        Bukkit.getOnlinePlayers().forEach { ChannelModule.getLobbyChannelGroup()?.fastJoin(it.uniqueId) }
                        delay(3000L)
                        GameCore.unsafe.rebootManager.reboot()
                    }
                }
            }

            // 재부팅 예약 처리
            else if (GameCore.unsafe.rebootManager.rebootScheduled) {
                GameCore.unsafe.rebootManager.reboot(GameCore.unsafe.rebootManager.rebootScheduledSender)
            }
        }
    }

    /**
     * 서버 재부팅 중 접속을 차단합니다.
     */
    @EventHandler
    fun onLogin(event: PlayerLoginEvent) {
        if (GameCore.unsafe.rebootManager.rebooting) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "서버 재부팅 중입니다.")
        }
    }

}