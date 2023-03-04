package net.pooleaf.gamecore.sidebar.elements

import net.pooleaf.core.modules.support.common.util.StringUtil
import net.pooleaf.core.modules.support.common.util.toMillis
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.sidebar.GameSideBarNamedTextElement

class GameTimeElement: GameSideBarNamedTextElement("§b진행 시간") {

    override var valueText: String
        get() {
            val time = GameCore.game.startedAt?.let { startedAt ->
                StringUtil.buildTimeStringFromMillis(System.currentTimeMillis() - startedAt.toMillis())
            } ?: ""

            return time.ifEmpty { "0초" }
        }
        set(value) {}

    override fun getPriority(): Int {
        return 100
    }

    override fun isShow(): Boolean {
        return GameCore.game.isGameStarted
    }

    override fun isUseSpace(): Boolean {
        return true
    }

}