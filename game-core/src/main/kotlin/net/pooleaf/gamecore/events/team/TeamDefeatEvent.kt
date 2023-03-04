package net.pooleaf.gamecore.events.team

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import net.pooleaf.gamecore.player.GamePlayer
import net.pooleaf.gamecore.team.Team

/**
 * 팀 탈락 시 호출됩니다.
 */
class TeamDefeatEvent(
    val team: Team,
    val killerGamePlayer: GamePlayer?
): HandlerEvent() {
}