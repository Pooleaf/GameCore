package net.pooleaf.gamereplay.sql.daos

import net.pooleaf.core.modules.sqllib.common.AbstractSqlManager
import net.pooleaf.core.modules.sqllib.common.SqlDao
import net.pooleaf.core.modules.sqllib.common.SqlTable
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.sql.dtos.ReplayDto
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.*

class ReplayDao(sqlManager: AbstractSqlManager?) : SqlDao(sqlManager) {

    val replayTable = SqlTable(sqlManager, "game_replays",
        "game_id VARCHAR(36) PRIMARY KEY",
        "created_at DATETIME",
        "end_tick INT",
        "replay_blob LONGBLOB"
    )

    override fun onConnected() {
        replayTable.create()
    }

    /**
     * Replay
     */

    fun insertReplay(replayDto: ReplayDto) {
        replayTable.insertInto()
            .values(
                replayDto.gameId,
                replayDto.createdAt,
                replayDto.endTick,
                replayDto.replayFile.readBytes()
            )
            .onDuplicateKeyUpdate()
            .execute()
    }

    fun selectReplay(gameId: String, download: Boolean): ReplayDto? {
        val result = replayTable.select()
            .where("game_id = ?")
            .parameters(gameId)
            .execute()

        if (result.rows.isEmpty()) return null

        val replayDto = ReplayDto(
            UUID.fromString(result.rows.get(0).getString("game_id")),
            result.rows.get(0).getLocalDateTime("created_at"),
            result.rows.get(0).getInt("end_tick").toLong(),
        )

        if (download) {
            GameReplayApi.unsafe.replayService.replayFolder.mkdirs()

            val blob = result.rows.get(0).getBlob("replay_blob")
            val blobBytes = blob.binaryStream.readBytes()

            if (replayDto.replayFile.exists()) {
                replayDto.replayFile.delete()
            }

            Files.write(replayDto.replayFile.toPath(), blobBytes, StandardOpenOption.CREATE_NEW)
        }

        return replayDto
    }

    /**
     * 최근순으로 리플레이 목록을 반환합니다.
     * 파일을 다운로드하지 않습니다.
     */
    fun selectReplayList(gameId: List<UUID>?, count: Int, offset: Int = 0): List<ReplayDto> {
        val context = replayTable.select()

        if (gameId != null) {
            val placeholders = gameId.joinToString("?", "(", ")")
            context.where("game_id IN ${placeholders}")
                .parameters(gameId)
        }

        val result = context.limit(offset, count)
            .orderBy("created_at", false)
            .execute()

        return result.rows.map {
            val gameId = it.getString("game_id")

            ReplayDto(
                UUID.fromString(gameId),
                it.getLocalDateTime("created_at"),
                it.getInt("end_tick").toLong(),
            )
        }
    }

    /**
     * 리플레이 개수를 반환합니다.
     */
    fun selectReplayCount(): Long {
        return replayTable.count()
    }

}