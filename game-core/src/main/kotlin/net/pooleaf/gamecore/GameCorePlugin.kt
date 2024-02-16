package net.pooleaf.gamecore

import kotlinx.coroutines.launch
import net.pooleaf.core.modules.commonevent.CommonEventModule
import net.pooleaf.core.modules.commonevent.common.CommonEventHandler
import net.pooleaf.core.modules.commonevent.common.CommonEventListener
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
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

        // 자동 재부팅 타이머 시작
        if (GameCore.unsafe.autoRebootConfig.useAutoReboot) {
            GameCore.unsafe.rebootManager.startAutoRebootTask()
        }

        GameCore.unsafe.antiCheatBypassService.init()

        Logger.log("플러그인이 초기화되었습니다. (게임 타입: ${GameCore.game.gameTypeId})")
    }

    override fun onEnd() {
        BukkitSyncScope.launch {
            if (GameCore.game.isGameStarted) {
                if (GameCore.unsafe.gameManager.canEnd()) {
                    GameCore.unsafe.gameManager.onGameEnd()
                } else {
                    GameCore.unsafe.gameManager.cancelGame(null, "서버 종료")
                }
            }
        }

        // 자동 재부팅 타이머 중지
        if (GameCore.unsafe.autoRebootConfig.useAutoReboot) {
            GameCore.unsafe.rebootManager.stopAutoRebootTask()
        }
    }

}