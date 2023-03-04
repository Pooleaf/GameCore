package net.pooleaf.gamehistory.sql.dtos

import net.pooleaf.core.modules.channel.ChannelModule
import net.pooleaf.gamecore.game.Game
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

fun Game.toDto(): GameDto {
    return GameDto(
        this.gameId?.toString() ?: error("gameId cannot be null"),
        this.gameTypeId,
        ChannelModule.getCurrentChannelName(),
        this.startedAt,
        this.endedAt,
        if (this.isCancelled) "Y" else "N"
    )
}