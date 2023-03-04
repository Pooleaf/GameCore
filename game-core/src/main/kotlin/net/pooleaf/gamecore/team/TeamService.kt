package net.pooleaf.gamecore.team

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.player.GamePlayer

class TeamService {

    /**
     * 팀에 플레이어를 추가합니다.
     * 성공 시 true, 실패 시 false를 반환합니다.
     */
    fun addPlayer(team: Team, gamePlayer: GamePlayer): Boolean {
        if (team.players.contains(gamePlayer)) return false
        team.players.add(gamePlayer); return true
    }

    /**
     * 팀에서 플레이어를 제거합니다.
     * 성공 시 true, 실패 시 false를 반환합니다.
     */
    fun removePlayer(team: Team, gamePlayer: GamePlayer): Boolean {
        val removed = team.players.remove(gamePlayer)

        if (team.players.isEmpty()) {
            GameCore.unsafe.teamManager.remove(team)
        }

        return removed
    }

    /**
     * [playerCountPerTeam]명끼리 묶어 팀을 생성합니다.
     * 만약 (플레이어 수 % [playerCountPerTeam])이 [playerCountPerTeam]보다 작을 경우 한 팀은 플레이어가 적게 생성됩니다.
     * 먄약 팀 수가 [maxTeamCount]에 도달할 경우 나머지 플레이어는 팀에 할당되지 않습니다.
     * 오프라인 플레이어를 포함합니다.
     */
    fun matchingTeams(playerCountPerTeam: Int, maxTeamCount: Int) {
        var tempTeam: Team? = null
        GameCore.unsafe.playerManager.getPlayingPlayers().forEach { gamePlayer ->
            // 새 팀 생성
            if (tempTeam == null) {
                // 최대 팀 수에 도달했을 경우 중단
                if (GameCore.unsafe.teamManager.teams.size >= maxTeamCount) {
                    return@forEach
                }

                tempTeam = Team(GameCore.unsafe.teamManager.teams.size)
                GameCore.unsafe.teamManager.add(tempTeam!!)
            }

            // 플레이어를 팀에 넣기
            gamePlayer.team = tempTeam
            addPlayer(tempTeam!!, gamePlayer)

            // 팀이 꽉차면 팀 null
            if (tempTeam!!.players.size >= GameCore.teamConfig.playerCountPerTeam) {
                tempTeam = null
            }
        }
    }

}