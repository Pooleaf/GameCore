package net.pooleaf.gamecore

import net.pooleaf.core.modules.commonevent.CommonEventModule
import net.pooleaf.core.modules.commonevent.common.CommonEventHandler
import net.pooleaf.core.modules.commonevent.common.CommonEventListener
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.core.plugin.BukkitCorePlugin
import net.pooleaf.gamecore.events.GameCoreInitializedEvent

class GameCorePlugin : BukkitCorePlugin(), CommonEventListener {

    override fun onStart() {
        prefix = "§c[ GameCore ]"
        color = CommonChatColor.RED
        registerLoggerPrefix()

        CommonEventModule.registerListener(this, this)
    }

    @CommonEventHandler
    fun onGameCoreInitialized(event: GameCoreInitializedEvent) {
        registerEventListeners()
        registerCommands()

        Logger.log("플러그인이 초기화되었습니다. (게임 타입: ${GameCore.game.gameTypeId})")
    }

}