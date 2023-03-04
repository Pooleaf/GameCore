package net.pooleaf.gamehistory.sql.dtos

data class GameWinnerDto(
    var gameId: String? = null,
    var teamId: Int = -1,
    var winnerPlayerUuid: String? = null
) {
}