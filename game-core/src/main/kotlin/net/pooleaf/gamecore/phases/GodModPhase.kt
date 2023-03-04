package net.pooleaf.gamecore.phases

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.delay
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.gamecore.Broadcaster
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.phase.Phase
import net.pooleaf.gamecore.utils.StringUtil

abstract class GodModPhase() : Phase() {

    var remainingGodModSeconds: Int? = null


    /**
     * 무적 지속 시간을 반환합니다.
     */
    abstract fun getGodModSeconds(): Int

    override fun onInit() {
        remainingGodModSeconds = null
    }

    override suspend fun onStart() {
        GameCore.game.isGodMode = true

        // 무적 알림
        val godModTime = StringUtil.buildTimeStringWithColor(getGodModSeconds() * 1000L, CommonChatColor.WHITE, CommonChatColor.YELLOW)

        Broadcaster.broadcast("")
        Broadcaster.broadcast("§e무적 시간이 시작되었습니다.")
        Broadcaster.broadcast("${godModTime} §e간 무적 상태가 지속됩니다.")
        Broadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3F, 0.7F)
    }

    override suspend fun onRun() {
        // 카운트
        for (count in getGodModSeconds() downTo 1) {
            remainingGodModSeconds = count

            if (count <= 5) {
                val remainingTime = StringUtil.buildTimeStringWithColor(count * 1000L, CommonChatColor.WHITE, CommonChatColor.YELLOW)

                Broadcaster.broadcast("${remainingTime} §e후 무적 시간이 종료됩니다.")
                Broadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3F, 0.7F)
            }

            GameCore.unsafe.sideBarManager.sideBar?.let { it.update() }

            delay(1000L)
        }
    }

    override fun onEnd() {
        GameCore.game.isGodMode = false

        Broadcaster.broadcast("")
        Broadcaster.broadcast("§e무적이 해제되었습니다.")
        Broadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3F, 0.7F)
    }

}