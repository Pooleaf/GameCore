package net.pooleaf.gamehistory.sql.dtos

data class GamePlayerStatsDto(
    var playerUuid: String? = null,
    var gameTypeId: Int = -1,
    var killCount: Int = -1,
    var deathCount: Int = -1,
    var assistCount: Int = -1,
    var winCount: Int = -1
)
