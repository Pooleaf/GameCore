package net.pooleaf.gamereplay.events

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import net.pooleaf.gamereplay.record.Record

/**
 * 리플레이 녹화 중 1틱 마다 호출됩니다.
 */
class RecordTickEvent(val record: Record) : HandlerEvent() {
}