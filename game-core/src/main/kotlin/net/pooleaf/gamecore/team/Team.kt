package net.pooleaf.gamecore.team

import net.pooleaf.core.modules.support.bukkit.util.TeleportUtil
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.player.GamePlayer
import org.bukkit.Location

/**
 * [GamePlayer]끼리 팀으로 묶어 우승을 함께하도록 만듭니다.
 * 팀간 PVP가 금지됩니다.
 */
data class Team(
    val id: Int = GameCore.unsafe.teamManager.teams.size,
    var teamName: String? = null
) {

    // 팀 소속 플레이어
    val players: ArrayList<GamePlayer> = ArrayList<GamePlayer>()

    // 게임 스폰 위치
    var spawnLocation: Location? = null


    /**
     * 팀에 플레이어를 추가합니다.
     * 성공 시 true, 실패 시 false를 반환합니다.
     */
    fun addPlayer(player: GamePlayer): Boolean {
        return GameCore.unsafe.teamService.addPlayer(this, player)
    }

    /**
     * 팀에서 플레이어를 제거합니다.
     * 성공 시 true, 실패 시 false를 반환합니다.
     */
    fun removePlayer(player: GamePlayer): Boolean {
        return GameCore.unsafe.teamService.removePlayer(this, player)
    }

    /**
     * 팀 탈락 여부를 확인합니다.
     */
    fun isDefeated(): Boolean {
        return players.size == players.filter { !it.isPlaying() }.size
    }

    /**
     * 팀에 소속된 플레이어를 해당 위치로 텔레포트시킵니다.
     * 온라인 플레이어만 텔레포트 됩니다.
     */
    fun teleport(location: Location) {
        players.filter { it.isOnline }
            .forEach { TeleportUtil.teleport(it.player, location) }
    }

}