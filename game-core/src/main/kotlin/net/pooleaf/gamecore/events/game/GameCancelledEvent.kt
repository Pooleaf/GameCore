package net.pooleaf.gamecore.events.game

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import org.bukkit.command.CommandSender

/**
 * 게임 중단 완료 이벤트
 * 게임 중단이 완전히 끝난 후 호출됩니다. (게임 리셋 후)
 */
class GameCancelledEvent(
    cancelSender: CommandSender?,
    cancelCause: String
): HandlerEvent() {
}