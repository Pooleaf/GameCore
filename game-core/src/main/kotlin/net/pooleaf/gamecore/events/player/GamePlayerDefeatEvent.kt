package net.pooleaf.gamecore.events.player

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import net.pooleaf.gamecore.player.GamePlayer

/**
 * 플레이어 탈락 시 호출됩니다.
 */
class GamePlayerDefeatEvent(
    val gamePlayer: GamePlayer,
    val killerGamePlayer: GamePlayer?,
    val assistGamePlayers: List<GamePlayer>?
): HandlerEvent() {
}