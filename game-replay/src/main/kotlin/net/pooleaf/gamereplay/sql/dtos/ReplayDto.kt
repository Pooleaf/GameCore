package net.pooleaf.gamereplay.sql.dtos

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.replay.Replay
import java.io.File
import java.time.LocalDateTime
import java.util.*

data class ReplayDto(
    val gameId: UUID,
    val createdAt: LocalDateTime,
    val endTick: Long
) {

    val replayFile: File
        get() = File(GameReplayApi.unsafe.replayService.replayFolder, "${gameId}.json")

}

fun Replay.toDto(): ReplayDto {
    return ReplayDto(
        this.gameId,
        this.createdAt!!,
        this.endTick
    )
}