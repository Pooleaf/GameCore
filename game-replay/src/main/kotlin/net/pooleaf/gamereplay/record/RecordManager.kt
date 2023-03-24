package net.pooleaf.gamereplay.record

import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.GameReplayPlugin
import net.pooleaf.gamereplay.data.datas.player.PlayerMoveData
import net.pooleaf.gamereplay.events.RecordStartEvent
import net.pooleaf.gamereplay.events.RecordStopEvent
import net.pooleaf.gamereplay.events.RecordTickEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import java.time.LocalDateTime
import java.util.*

class RecordManager {

    var record: Record? = null

    var recordTickCalculateTask: BukkitTask? = null


    /**
     * 녹화 중 여부를 반환합니다.
     */
    fun isRecording(): Boolean {
        return record?.let { it.isRecording } == true
    }

    /**
     * 녹화를 시작합니다.
     */
    fun startRecord(
        gameUuid: UUID,
        recordTargetPlayers: List<UUID>,
        worldName: String,
        x: Double,
        y: Double,
        z: Double
    ) {
        if (isRecording()) error("Recording already started")

        record = Record(gameUuid, recordTargetPlayers, worldName, x, y, z)
        record?.let { record ->
            record.isRecording = true
            record.replay.createdAt = LocalDateTime.now()

            // 플레이어 초기 데이터
            recordTargetPlayers.forEach { uuid ->
                val player = Bukkit.getPlayer(uuid)
                if (player == null) return@forEach

                val location = player.location

                val playerMoveData = PlayerMoveData().apply {
                    playerUuid = uuid
                    this.worldName = location.world.name
                    this.x = location.x
                    this.y = location.y
                    this.z = location.z
                    yaw = location.yaw
                    pitch = location.pitch
                }
                record.addRecordData(playerMoveData)
            }

            recordTickCalculateTask = Bukkit.getScheduler().runTaskTimer(GameReplayPlugin.instance, {
                if (!isRecording()) return@runTaskTimer

                // 이벤트
                Bukkit.getPluginManager().callEvent(RecordTickEvent(record))

                // 틱 계산
                record.currentTick++
            }, 0L, 1L)

            // 이벤트
            Bukkit.getPluginManager().callEvent(RecordStartEvent(record))
        }

        Logger.log("${gameUuid} 녹화가 시작되었습니다.")
    }

    /**
     * 녹화를 중지합니다.
     */
    fun endRecord() {
        if (!isRecording()) error("Recording not started")

        record?.let { record ->
            record.isRecording = false
            record.replay.endTick = record.currentTick.toLong()

            recordTickCalculateTask?.cancel()

            // 저장
            GameReplayApi.unsafe.replayService.saveReplay(record.replay)
            GameReplayApi.unsafe.replayManager.set(record.replay.gameId, record.replay)

            // 이벤트
            Bukkit.getPluginManager().callEvent(RecordStopEvent(record))

            Logger.log("${record.replay.gameId} 녹화가 종료되었습니다.")

            this.record = null
        }
    }

    /**
     * 녹화 대상 플레이어인지를 반환합니다.
     */
    fun isRecordingTargetPlayer(player: Player): Boolean {
        return record?.let { it.recordTargetPlayers.contains(player.uniqueId) } == true
                && GameCore.unsafe.playerManager.get(player.uniqueId).isPlaying()
    }

}