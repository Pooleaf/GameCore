package net.pooleaf.gamehistory.sql.dtos

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.player.GamePlayer

data class GameParticipantDto(
    var gameId: String? = null,
    var teamId: Int = -1,
    var playerUuid: String? = null
) {
}

fun GamePlayer.toDto(): GameParticipantDto {
    var gameId = GameCore.game.gameId?.toString() ?: error("gameId cannot be null")
    var teamId = this.team?.id ?: error("teamId cannot be null")

    return GameParticipantDto(gameId, teamId, this.uuid.toString())
}