package net.pooleaf.gamecore.events.player

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import net.pooleaf.gamecore.player.GamePlayer

/**
 * 플레이어가 게임 참여에서 제외될 때 호출됩니다.
 */
class GamePlayerQuitFromGameEvent(val gamePlayer: GamePlayer): HandlerEvent() {
}