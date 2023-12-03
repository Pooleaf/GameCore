package net.pooleaf.gamecore.sidebar.elements

import net.pooleaf.core.modules.support.common.util.StringUtil
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.phases.GodModePhase
import net.pooleaf.gamecore.sidebar.GameSideBarNamedTextElement

class GodModeRemainingTimeElement: GameSideBarNamedTextElement("§6무적 해제까지") {

    override var valueText: String
        get() = (GameCore.game.phasePipeline.currentPhase as GodModePhase).remainingGodModeSeconds?.let { remainingGodModeSeconds ->
            StringUtil.buildTimeStringFromSeconds(remainingGodModeSeconds.toLong())
        } ?: ""
        set(value) {}

    override fun getPriority(): Int {
        return 10
    }

    override fun isShow(): Boolean {
        val phase = GameCore.game.phasePipeline.currentPhase
        return phase is GodModePhase && phase.remainingGodModeSeconds != null
    }

    override fun isUseSpace(): Boolean {
        return true
    }

}