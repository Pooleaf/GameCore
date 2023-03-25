package net.pooleaf.gamereplay.replay

import net.pooleaf.core.modules.channel.ChannelModule
import net.pooleaf.core.modules.support.bukkit.util.TeleportUtil
import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.core.modules.support.common.util.GsonUtil
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.GameReplayPlugin
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.sql.dtos.ReplayDto
import net.pooleaf.gamereplay.sql.dtos.toDto
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.*

class ReplayService {

    val replayFolder = File(GameReplayPlugin.instance.dataFolder, "replay")

    val gson = GsonUtil.gsonBuilder
        .registerTypeHierarchyAdapter(RecordData::class.java, RecordDataDeserializer())
        .create()


    fun saveReplay(replay: Replay) {
        saveReplayToFile(replay)
        saveReplayToDatabase(replay)
    }

    fun saveReplayToFile(replay: Replay) {
        val json = gson.toJson(replay)

        replayFolder.mkdirs()

        val file = File(replayFolder, "${replay.gameId}.json")
        Files.write(file.toPath(), json.toByteArray(), StandardOpenOption.CREATE_NEW)

        Logger.log("${replay.gameId} 녹화 파일을 저장했습니다.")
    }

    fun saveReplayToDatabase(replay: Replay) {
        GameReplayApi.unsafe.sqlManager.replayDao.insertReplay(replay.toDto())

        Logger.log("${replay.gameId} 녹화를 DB에 저장했습니다.")
    }

    /**
     * 리플레이를 재생합니다.
     * 서버에 캐싱되지 않은 리플레이라면 DB에서 불러옵니다.
     * Primary Thread에서만 사용할 수 있습니다.
     */
    fun playReplay(viewer: Player, gameId: UUID, tick: Long = 0): ReplayPlayer {
        if (!Bukkit.isPrimaryThread()) error("playReplay can only be used in primary thread")
        if (isPlayingReplay(viewer)) error("Player already watching replay")

        // DB에서 리플레이 불러오기
        if (!GameReplayApi.unsafe.replayManager.exists(gameId)) {
            viewer.sendMessage("${gameId} §e리플레이를 불러오는 중입니다..")
            GameReplayApi.unsafe.replayService.loadReplayFromDatabase(gameId)
        }

        val replay = GameReplayApi.unsafe.replayManager.get(gameId) ?: error("Replay ${gameId} is not exist")

        // 재생
        val replayPlayer = ReplayPlayer(viewer, replay)
        GameReplayApi.unsafe.replayPlayerManager.set(viewer.uniqueId, replayPlayer)

        replayPlayer.init()
        replayPlayer.play()

        // 건너뛰기
        if (tick > 0) {
            replayPlayer.jumpTo(tick)
        }

        viewer.sendMessage("${replayPlayer.replay.gameId} §e리플레이를 재생합니다.")

        return replayPlayer
    }

    /**
     * 리플레이를 종료합니다.
     * Primary Thread에서만 사용할 수 있습니다.
     */
    fun exitReplay(viewer: Player, sendToLobby: Boolean = true): ReplayPlayer {
        if (!Bukkit.isPrimaryThread()) error("playReplay can only be used in primary thread")
        if (!isPlayingReplay(viewer)) error("Player not watching replay")

        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)
        replayPlayer.exit()

        GameReplayApi.unsafe.replayPlayerManager.remove(viewer.uniqueId)

        // 뷰어 텔레포트
        GameReplayApi.spawnConfig.spawnLocation?.let { spawnLocation -> TeleportUtil.teleport(viewer, spawnLocation) }

        if (sendToLobby) {
            // 뷰어 로비로 이동
            if (GameReplayApi.replayConfig.isReplayPlayServer) {
                ChannelModule.getLobbyChannelGroup().fastJoin(viewer.uniqueId)
            }
        }

        viewer.sendMessage("${replayPlayer.replay.gameId} §e리플레이를 종료합니다.")

        return replayPlayer
    }

    fun isPlayingReplay(viewer: Player): Boolean {
        return GameReplayApi.unsafe.replayPlayerManager.exists(viewer.uniqueId)
    }

    fun selectReplayDtoNoCache(gameId: UUID, download: Boolean): ReplayDto? {
        return GameReplayApi.unsafe.sqlManager.replayDao.selectReplay(gameId.toString(), download)
    }

    /**
     * DB에서 리플레이 목록을 불러와 반환합니다.
     * 파일을 다운로드 하지 않습니다.
     */
    fun selectReplayDtoListNoCache(gameId: List<UUID>?, count: Int, offset: Int = 0): List<ReplayDto> {
        return GameReplayApi.unsafe.sqlManager.replayDao.selectReplayList(gameId, count, offset)
    }

    /**
     * 리플레이 개수를 반환합니다.
     */
    fun selectReplayCount(): Long {
        return GameReplayApi.unsafe.sqlManager.replayDao.selectReplayCount()
    }

    fun loadReplayFromFile(gameId: UUID): Replay? {
        val file = File(replayFolder, "${gameId}.json")
        if (!file.exists()) return null

        val json = file.readLines().joinToString(" ")
        return gson.fromJson(json, Replay::class.java)
    }

    fun loadReplayFromDatabase(gameId: UUID): Replay? {
        val replayDto = selectReplayDtoNoCache(gameId, true)
        val replay = loadReplayFromFile(gameId)
        GameReplayApi.unsafe.replayManager.set(gameId, replay)

        return replay
    }

}