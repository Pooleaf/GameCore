package net.pooleaf.gamereplay

import net.pooleaf.gamereplay.configs.ReplayConfig
import net.pooleaf.gamereplay.configs.SpawnConfig
import net.pooleaf.gamereplay.record.RecordManager
import net.pooleaf.gamereplay.replay.RecordDataManager
import net.pooleaf.gamereplay.replay.ReplayManager
import net.pooleaf.gamereplay.replay.ReplayPlayerManager
import net.pooleaf.gamereplay.replay.ReplayService
import net.pooleaf.gamereplay.sql.GameReplaySqlManager
import java.io.File

object GameReplayApi {

    object unsafe {
        lateinit var recordManager: RecordManager
        lateinit var recordDataManager: RecordDataManager

        lateinit var replayManager: ReplayManager
        lateinit var replayService: ReplayService

        lateinit var replayPlayerManager: ReplayPlayerManager

        lateinit var sqlManager: GameReplaySqlManager


        val replayConfig: ReplayConfig by lazy {
            ReplayConfig(File(GameReplayPlugin.instance.dataFolder, "replay-config.yml"))
        }

        val spawnConfig: SpawnConfig by lazy {
            SpawnConfig(File(GameReplayPlugin.instance.dataFolder, "spawn-config.yml"))
        }

        const val REPLAY_CHANNEL_GROUP: String = "replay"


        fun init() {
            recordManager = RecordManager()
            recordDataManager = RecordDataManager()

            replayManager = ReplayManager()
            replayService = ReplayService()

            replayPlayerManager = ReplayPlayerManager()

            sqlManager = GameReplaySqlManager()

            loadConfig()

            if (replayConfig.isRecordServer) {
                recordDataManager.registerRecordListeners()
            }

            if (replayConfig.isReplayPlayServer) {
                recordDataManager.registerReplayHandlers()
            }
        }

        fun loadConfig() {
            replayConfig.load()
            replayConfig.save()

            spawnConfig.load()
            spawnConfig.save()
        }
    }


    val replayConfig
        get() = unsafe.replayConfig

    val spawnConfig
        get() = unsafe.spawnConfig


    fun init() {
        unsafe.init()
    }

    fun loadConfig() {
        unsafe.loadConfig()
    }

}