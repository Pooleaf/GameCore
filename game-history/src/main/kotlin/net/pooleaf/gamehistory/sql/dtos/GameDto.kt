package net.pooleaf.gamehistory.sql.dtos

import java.time.LocalDateTime

data class GameDto(
    var gameId: String? = null,
    var gameTypeId: Int = -1,
    var channelName: String? = null,
    var startedAt: LocalDateTime? = null,
    var endedAt: LocalDateTime? = null,
    var cancelYn: String? = null
) {
}