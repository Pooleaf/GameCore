package net.pooleaf.gamecore.sidebar.elements

import net.pooleaf.core.modules.support.common.util.StringUtil
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.phases.GodModPhase
import net.pooleaf.gamecore.sidebar.GameSideBarNamedTextElement

class GodModRemainingTimeElement: GameSideBarNamedTextElement("§6무적 해제까지") {

    override var valueText: String
        get() = (GameCore.game.phasePipeline.currentPhase as GodModPhase).remainingGodModSeconds?.let { remainingGodModSeconds ->
            StringUtil.buildTimeStringFromSeconds(remainingGodModSeconds.toLong())
        } ?: ""
        set(value) {}

    override fun getPriority(): Int {
        return 10
    }

    override fun isShow(): Boolean {
        val phase = GameCore.game.phasePipeline.currentPhase
        return phase is GodModPhase && phase.remainingGodModSeconds != null
    }

    override fun isUseSpace(): Boolean {
        return true
    }

}