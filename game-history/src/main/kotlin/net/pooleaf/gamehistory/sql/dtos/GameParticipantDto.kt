package net.pooleaf.gamehistory.sql.dtos

data class GameParticipantDto(
    var gameId: String? = null,
    var teamId: Int = -1,
    var playerUuid: String? = null,
    var defeatYn: String? = null
) {
}