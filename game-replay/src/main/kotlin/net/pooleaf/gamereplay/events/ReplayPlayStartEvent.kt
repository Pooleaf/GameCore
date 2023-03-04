package net.pooleaf.gamereplay.events

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import net.pooleaf.gamereplay.replay.ReplayPlayer

/**
 * 리플레이 재상 시작 이벤트
 */
class ReplayPlayStartEvent(val replayPlayer: ReplayPlayer) : HandlerEvent() {
}