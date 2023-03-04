package net.pooleaf.gamecore.events.player

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import net.pooleaf.gamecore.player.GamePlayer
import org.bukkit.event.player.PlayerJoinEvent

/**
 * 플레이어 접속 시 호출됩니다.
 */
class GamePlayerJoinEvent(
    val gamePlayer: GamePlayer,
    val playerJoinEvent: PlayerJoinEvent
): HandlerEvent() {
}