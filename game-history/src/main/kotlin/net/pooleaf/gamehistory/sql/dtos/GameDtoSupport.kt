package net.pooleaf.gamehistory.sql.dtos

import net.pooleaf.core.modules.channel.ChannelModule
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.game.Game
import net.pooleaf.gamecore.player.GamePlayer

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

fun GamePlayer.toDto(): GameParticipantDto {
    var gameId = GameCore.game.gameId?.toString() ?: error("gameId cannot be null")
    var teamId = this.team?.id ?: error("teamId cannot be null")
    val defeatYn = if (isDefeated) "Y" else "N"
    val teamDefeatYn = if (team!!.isDefeated()) "Y" else "N"

    return GameParticipantDto(gameId, teamId, this.uuid.toString(), defeatYn, teamDefeatYn)
}