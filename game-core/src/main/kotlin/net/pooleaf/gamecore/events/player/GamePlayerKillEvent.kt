package net.pooleaf.gamecore.events.player

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.PlayerDamageEvent
import net.pooleaf.gamecore.player.GamePlayer
import org.bukkit.event.entity.PlayerDeathEvent

/**
 * 플레이어가 다른 플레이어를 죽일 시 호출됩니다.
 */
class GamePlayerKillEvent(
    val killerGamePlayer: GamePlayer,
    val deadGamePlayer: GamePlayer,
    val playerDamageEvent: PlayerDamageEvent
): HandlerEvent() {
}