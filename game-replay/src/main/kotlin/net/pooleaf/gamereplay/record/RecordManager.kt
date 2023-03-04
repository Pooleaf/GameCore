package net.pooleaf.gamereplay.record

import com.comphenix.protocol.ProtocolLibrary
import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamereplay.events.RecordTickEvent
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.block.BlockChangeDataRecordListener
import net.pooleaf.gamereplay.data.block.MultiBlockChangeDataRecordListener
import net.pooleaf.gamereplay.data.entity.*
import net.pooleaf.gamereplay.data.player.PlayerMetaDataDataRecordListener
import net.pooleaf.gamereplay.data.player.PlayerMoveData
import net.pooleaf.gamereplay.events.RecordStartEvent
import net.pooleaf.gamereplay.events.RecordStopEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import java.time.LocalDateTime
import java.util.*

class RecordManager {

    var record: Record? = null

    var recordTickCalculateTask: BukkitTask? = null


    fun registerRecordListeners() {
//        ProtocolLibrary.getProtocolManager().addPacketListener(TestPacketListener()) // TODO remove

        // Block
        ProtocolLibrary.getProtocolManager().addPacketListener(BlockChangeDataRecordListener())
        ProtocolLibrary.getProtocolManager().addPacketListener(MultiBlockChangeDataRecordListener())

        // Entity
        ProtocolLibrary.getProtocolManager().addPacketListener(CollectDataRecordListener())
        ProtocolLibrary.getProtocolManager().addPacketListener(EntityDestroyDataRecordListener())
        ProtocolLibrary.getProtocolManager().addPacketListener(EntityVelocityDataRecordListener())
        ProtocolLibrary.getProtocolManager().addPacketListener(ItemMetaDataDataRecordListener())
        ProtocolLibrary.getProtocolManager().addPacketListener(SpawnEntityDataRecordListener())

        // Player
        ProtocolLibrary.getProtocolManager().addPacketListener(PlayerMetaDataDataRecordListener())
    }

    /**
     * 녹화 중 여부를 반환합니다.
     */
    fun isRecording(): Boolean {
        return record?.let { it.isRecording } == true
    }

    /**
     * 녹화를 시작합니다.
     */
    fun startRecord(gameUuid: UUID, recordTargetPlayers: List<UUID>) {
        if (isRecording()) error("Recording already started")

        record = Record(gameUuid, recordTargetPlayers)
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
                    worldName = location.world.name
                    x = location.x
                    y = location.y
                    z = location.z
                    yaw = location.yaw
                    pitch = location.pitch
                }
                record.addRecordData(playerMoveData)
            }

            recordTickCalculateTask = Bukkit.getScheduler().runTaskTimer(GameCore.gamePlugin, {
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
    }

}