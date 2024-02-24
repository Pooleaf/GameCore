package net.pooleaf.gamecore.phases

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.delay
import net.pooleaf.core.modules.support.bukkit.util.BukkitBroadcaster
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.phase.Phase
import net.pooleaf.gamecore.utils.StringUtil

abstract class GodModePhase : Phase() {

    var remainingGodModeSeconds: Int = 0


    /**
     * 무적 지속 시간을 반환합니다.
     */
    abstract fun getGodModeSeconds(): Int

    override fun onInit() {
        remainingGodModeSeconds = getGodModeSeconds()
    }

    override suspend fun onStart() {
        GameCore.game.isGodMode = true

        // 무적 알림
        val godModeTime = StringUtil.buildTimeStringWithColor(getGodModeSeconds() * 1000L, CommonChatColor.WHITE, CommonChatColor.YELLOW)

        BukkitBroadcaster.broadcast("")
        BukkitBroadcaster.broadcast("§e무적 시간이 시작되었습니다.")
        BukkitBroadcaster.broadcast("${godModeTime} §e간 무적 상태가 지속됩니다.")
        BukkitBroadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3F, 0.7F)
    }

    override suspend fun onRun() {
        // 카운트
        while (remainingGodModeSeconds >= 1) {
            if (remainingGodModeSeconds <= 5) {
                val remainingTime = StringUtil.buildTimeStringWithColor(remainingGodModeSeconds * 1000L, CommonChatColor.WHITE, CommonChatColor.YELLOW)

                BukkitBroadcaster.broadcast("${remainingTime} §e후 무적 시간이 종료됩니다.")
                BukkitBroadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3F, 0.7F)
            }

            GameCore.unsafe.sideBarManager.sideBar?.update()

            delay(1000L)
            remainingGodModeSeconds--
        }
    }

    override fun onEnd() {
        GameCore.game.isGodMode = false

        BukkitBroadcaster.broadcast("")
        BukkitBroadcaster.broadcast("§e무적이 해제되었습니다.")
        BukkitBroadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3F, 0.7F)
    }

}