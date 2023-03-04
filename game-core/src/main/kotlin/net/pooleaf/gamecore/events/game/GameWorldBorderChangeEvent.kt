package net.pooleaf.gamecore.events.game

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import org.bukkit.Location

/**
 * 게임 경계선 변경 이벤트
 */
class GameWorldBorderChangeEvent(
    val centerLocation: Location,
    val beforeSize: Int,
    val newSize: Int,
    val updateDurationSeconds: Int
): HandlerEvent() {
}