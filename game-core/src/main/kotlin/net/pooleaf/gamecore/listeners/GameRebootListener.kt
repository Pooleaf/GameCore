package net.pooleaf.gamecore.listeners

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.game.GameResetEvent
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
        if (!GameCore.unsafe.rebootManager.rebooting && GameCore.unsafe.rebootManager.rebootScheduled) {
            GameCore.unsafe.rebootManager.reboot(GameCore.unsafe.rebootManager.rebootScheduledSender)
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