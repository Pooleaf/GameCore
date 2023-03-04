package net.pooleaf.gamecore.events.player

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import net.pooleaf.gamecore.player.GamePlayer

/**
 * 플레이어 정보 초기화 시 호출됩니다.
 */
class GamePlayerInitEvent(val gamePlayer: GamePlayer): HandlerEvent() {
}