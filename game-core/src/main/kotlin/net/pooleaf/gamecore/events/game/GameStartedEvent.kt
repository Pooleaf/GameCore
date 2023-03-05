package net.pooleaf.gamecore.events.game

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent

/**
 * 게임 시작 완료 시 호출됩니다.
 * Async로 실행되기 때문에 플레이어가 맵에 텔레포트 되기 전에 실행됩니다.
 */
class GameStartedEvent: HandlerEvent() {
}