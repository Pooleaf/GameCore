package net.pooleaf.gamereplay

import net.pooleaf.core.modules.commonevent.CommonEventModule
import net.pooleaf.core.modules.commonevent.common.CommonEventHandler
import net.pooleaf.core.modules.commonevent.common.CommonEventListener
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.core.plugin.BukkitCorePlugin
import net.pooleaf.gamecore.events.GameCoreInitializedEvent

class GameReplayPlugin : BukkitCorePlugin(), CommonEventListener {

    companion object {
        lateinit var instance: GameReplayPlugin
    }


    override fun onStart() {
        instance = this

        prefix = "§c[ GameReplay ]"
        color = CommonChatColor.RED
        registerLoggerPrefix()

        CommonEventModule.registerListener(this, this)
    }

    @CommonEventHandler
    fun onGameCoreInitialized(event: GameCoreInitializedEvent) {
        GameReplayApi.init()

        GameReplayApi.unsafe.sqlManager.connect()
        loadConfig()

        registerEventListeners()
        registerCommands()

        Logger.log("플러그인이 초기화되었습니다.")
    }

    override fun onEnd() {
        GameReplayApi.unsafe.sqlManager.close()
    }

    override fun onConfigLoaded() {
        GameReplayApi.loadConfig()
    }


}