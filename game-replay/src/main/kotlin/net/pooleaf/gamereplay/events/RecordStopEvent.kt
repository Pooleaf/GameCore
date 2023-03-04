package net.pooleaf.gamereplay.events

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import net.pooleaf.gamereplay.record.Record

/**
 * 리플레이 녹화 종료 후 실행됩니다.
 */
class RecordStopEvent(val record: Record) : HandlerEvent() {
}