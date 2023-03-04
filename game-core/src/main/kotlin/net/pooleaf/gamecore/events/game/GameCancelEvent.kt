package net.pooleaf.gamecore.events.game

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import org.bukkit.command.CommandSender

/**
 * 게임 중단 이벤트
 * 게임 중단 정보 업데이트 직후 호출됩니다.
 */
class GameCancelEvent(
    cancelSender: CommandSender?,
    cancelCause: String
): HandlerEvent() {
}