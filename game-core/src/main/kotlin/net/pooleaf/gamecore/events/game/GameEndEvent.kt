package net.pooleaf.gamecore.events.game

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import net.pooleaf.gamecore.team.Team

/**
 * 게임 종료 시 호출됩니다.
 */
class GameEndEvent(
    val winnerTeam: Team?
): HandlerEvent() {
}