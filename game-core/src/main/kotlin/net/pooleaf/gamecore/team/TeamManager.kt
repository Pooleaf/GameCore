package net.pooleaf.gamecore.team

import net.pooleaf.gamecore.player.GamePlayer

class TeamManager() {

    val teams: ArrayList<Team> = ArrayList<Team>()


    /**
     * [team]을 추가합니다.
     * 성공 시 true, 실패 시 false를 반환합니다.
     */
    fun add(team: Team): Boolean {
        if (teams.contains(team)) return false
        teams.add(team); return true
    }

    /**
     * [team]을 삭제합니다.
     * 성공 시 true, 실패 시 false를 반환합니다.
     */
    fun remove(team: Team): Boolean {
        return teams.remove(team)
    }

    /**
     * [team] 존재 여부를 확인합니다.
     */
    fun exists(team: Team): Boolean {
        return teams.contains(team)
    }

    /**
     * [gamePlayer]가 소속된 [Team]을 반환합니다.
     * 없을 경우 null을 반환합니다.
     */
    fun getHasGamePlayer(gamePlayer: GamePlayer): Team? {
        return teams.firstOrNull { it.players.contains(gamePlayer) }
    }

    /**
     * 탈락하지 않고 온라인 상태인 [Team] 목록을 반환합니다.
     */
    fun getNotDefeatedOnlineTeams(): List<Team> {
        return teams.filter { team ->
            !team.isDefeated()
                    && team.players.filter { player -> player.isOnline }.isNotEmpty()
        }
    }

}