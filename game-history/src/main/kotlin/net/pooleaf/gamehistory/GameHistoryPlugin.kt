package net.pooleaf.gamehistory

import net.pooleaf.core.modules.commonevent.CommonEventModule
import net.pooleaf.core.modules.commonevent.common.CommonEventHandler
import net.pooleaf.core.modules.commonevent.common.CommonEventListener
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.core.plugin.BukkitCorePlugin
import net.pooleaf.gamecore.events.GameCoreInitializedEvent

class GameHistoryPlugin : BukkitCorePlugin(), CommonEventListener {

    companion object {
        lateinit var instance: GameHistoryPlugin
    }


    override fun onStart() {
        instance = this

        prefix = "§c[ GameHistory ]"
        color = CommonChatColor.RED
        registerLoggerPrefix()

        CommonEventModule.registerListener(this, this)
    }

    @CommonEventHandler
    fun onGameCoreInitialized(event: GameCoreInitializedEvent) {
        GameHistoryApi.init()

        GameHistoryApi.unsafe.sqlManager.connect()
        loadConfig()

        registerEventListeners()

        Logger.log("플러그인이 초기화되었습니다.")
    }

    override fun onEnd() {
        GameHistoryApi.unsafe.sqlManager.close()
    }

    override fun onConfigLoaded() {
        GameHistoryApi.loadConfig()
    }

}