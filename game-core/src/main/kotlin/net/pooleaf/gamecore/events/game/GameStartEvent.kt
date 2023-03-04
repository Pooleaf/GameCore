package net.pooleaf.gamecore.events.game

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import org.bukkit.command.CommandSender

/**
 * 게임 시작 시 호출됩니다.
 * 자동으로 시작한 게임일 경우 [startSender]이 null로 할당됩니다.
 */
class GameStartEvent(
    val starterSender: CommandSender?
): HandlerEvent() {
}