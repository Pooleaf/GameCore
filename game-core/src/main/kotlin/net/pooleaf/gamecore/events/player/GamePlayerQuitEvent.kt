package net.pooleaf.gamecore.events.player

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import net.pooleaf.gamecore.player.GamePlayer
import org.bukkit.event.player.PlayerQuitEvent

/**
 * 플레이어 퇴장 시 호출됩니다.
 */
class GamePlayerQuitEvent(
    val gamePlayer: GamePlayer,
    val playerQuitEvent: PlayerQuitEvent
): HandlerEvent() {
}