package net.pooleaf.gamecore.team

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.player.GamePlayer
import net.pooleaf.party.common.PartyApi


class TeamService {

    /**
     * 팀에 플레이어를 추가합니다.
     * 성공 시 true, 실패 시 false를 반환합니다.
     */
    fun addPlayer(team: Team, gamePlayer: GamePlayer): Boolean {
        if (team.players.contains(gamePlayer)) return false
        team.players.add(gamePlayer)

        // 이름표 접두사 팀 표시 보여주기
        if (gamePlayer.team!!.players.size > 1) {
            team.players.forEach { GameCore.unsafe.teamNameTagManager.setTeamNameTag(it) }
        }

        return true
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

        // 이름표 접두사 팀 표시 제거
        if (gamePlayer.team!!.players.isNotEmpty()) {
            team.players.filter { it.isOnline }
                .forEach { GameCore.unsafe.teamNameTagManager.setTeamNameTag(it) }

            if (gamePlayer.isOnline) {
                GameCore.unsafe.teamNameTagManager.removeTeamNameTag(gamePlayer)
            }
        }

        return removed
    }

    /**
     * [playerCountPerTeam]명씩 묶어 팀을 생성합니다.
     * 파티에 소속된 플레이어는 파티원끼리 팀이 생성되고, 파티 인원이 부족할 경우 다른 플레이어를 포함시켜 팀이 생성됩니다.
     * 만약 (플레이어 수 % [playerCountPerTeam])이 [playerCountPerTeam]보다 작을 경우 한 팀은 플레이어가 적게 생성됩니다.
     * 먄약 팀 수가 [maxTeamCount]에 도달할 경우 나머지 플레이어는 팀에 할당되지 않습니다.
     * 오프라인 플레이어를 포함합니다.
     */
    fun matchingTeams(playerCountPerTeam: Int, maxTeamCount: Int) {
        var tempTeams = arrayListOf<Team>()
        for (i in 0 until maxTeamCount) {
            tempTeams.add(Team(i))
        }

        var tempTeam: Team? = null
        var tempTeamIndex = 0

        // 파티끼리 매칭
        PartyApi.unsafe.partyManager.values().forEach { party ->
            party.playerUuids.mapNotNull { GameCore.unsafe.playerManager.get(it) }
                .filter { it.isPlaying() }
                .shuffled()
                .forEach { gamePlayer ->
                    // 새 팀 생성
                    if (tempTeam == null) {
                        // 최대 팀 수에 도달했을 경우 중단
                        if (tempTeamIndex >= maxTeamCount) {
                            return@forEach
                        }

                        tempTeam = tempTeams.get(tempTeamIndex)
                    }

                    // 플레이어를 팀에 넣기
                    gamePlayer.team = tempTeam
                    addPlayer(tempTeam!!, gamePlayer)

                    // 팀이 꽉차면 팀 null
                    if (tempTeam!!.players.size >= playerCountPerTeam) {
                        tempTeam = null
                        tempTeamIndex++;
                    }
                }
        }

        // 나머지 팀 매칭
        tempTeamIndex = 0

        GameCore.unsafe.playerManager.getPlayingPlayers()
            .filter { it.team == null }
            .shuffled()
            .forEach { gamePlayer ->
                // 새 팀 생성
                if (tempTeam == null) {
                    // 최대 팀 수에 도달했을 경우 중단
                    if (tempTeamIndex >= maxTeamCount) {
                        return@forEach
                    }

                    // 가득 차지 않은 팀을 찾고, 없을 경우 중단
                    tempTeam = tempTeams.firstOrNull { it.players.size < playerCountPerTeam } ?: return@forEach
                }

                // 플레이어를 팀에 넣기
                gamePlayer.team = tempTeam
                addPlayer(tempTeam!!, gamePlayer)

                // 팀이 꽉차면 팀 null
                if (tempTeam!!.players.size >= playerCountPerTeam) {
                    tempTeam = null
                }
            }

        // 플레이어가 있는 팀만 등록
        tempTeams.filter { it.players.isNotEmpty() }
            .forEach { GameCore.unsafe.teamManager.add(it) }
    }

}