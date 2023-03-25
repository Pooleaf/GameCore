package net.pooleaf.gamereplay

import com.cryptomorin.xseries.XSound
import net.md_5.bungee.api.chat.ClickEvent
import net.pooleaf.core.modules.support.common.component.SimpleComponentBuilder
import net.pooleaf.gamereplay.channel.ChannelManager
import net.pooleaf.gamereplay.configs.ReplayConfig
import net.pooleaf.gamereplay.configs.SpawnConfig
import net.pooleaf.gamereplay.record.RecordManager
import net.pooleaf.gamereplay.replay.*
import net.pooleaf.gamereplay.sql.GameReplaySqlManager
import org.bukkit.entity.Player
import java.io.File

object GameReplayApi {

    object unsafe {
        lateinit var recordManager: RecordManager
        lateinit var recordDataManager: RecordDataManager

        lateinit var replayManager: ReplayManager
        lateinit var replayService: ReplayService

        lateinit var replayPlayerManager: ReplayPlayerManager

        lateinit var channelManager: ChannelManager

        lateinit var sqlManager: GameReplaySqlManager


        val replayConfig: ReplayConfig by lazy {
            ReplayConfig(File(GameReplayPlugin.instance.dataFolder, "replay-config.yml"))
        }

        val spawnConfig: SpawnConfig by lazy {
            SpawnConfig(File(GameReplayPlugin.instance.dataFolder, "spawn-config.yml"))
        }

        fun init() {
            recordManager = RecordManager()
            recordDataManager = RecordDataManager()

            replayManager = ReplayManager()
            replayService = ReplayService()

            replayPlayerManager = ReplayPlayerManager()

            channelManager = ChannelManager()

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

    /**
     * 리플레이 공유 메시지를 보냅니다.
     */
    fun shareReplay(player: Player, replay: Replay, tick: Long = 0) {
        player.sendMessage("")
        player.sendMessage(SimpleComponentBuilder("§e리플레이를 공유하려면 §6§l[여기]§e를 클릭하고 명령어를 복사하세요.")
            .clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/리플레이 재생 ${replay.gameId}")
            .hoverShowText("클릭 시 명령어를 복사하여 다른사람에게 공유하세요!")
            .build())
        if (tick > 0) {
            player.sendMessage(SimpleComponentBuilder("§e현재 재생시간의 리플레이를 공유하려면 §6§l[여기]§e를 클릭하고 명령어를 복사하세요.")
                .clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/리플레이 재생 ${replay.gameId} ${tick}")
                .hoverShowText("클릭 시 명령어를 복사하여 다른사람에게 공유하세요!")
                .build())
        }
        XSound.UI_BUTTON_CLICK.play(player, 0.3F, 0.7F)
    }

}