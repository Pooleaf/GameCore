package net.pooleaf.gamehistory.sql.dtos

import java.time.LocalDateTime

data class GameKillDto(
    var gameId: String? = null,
    var killerPlayerUuid: String? = null,
    var deadPlayerUuid: String? = null,
    var killedAt: LocalDateTime? = null
)
