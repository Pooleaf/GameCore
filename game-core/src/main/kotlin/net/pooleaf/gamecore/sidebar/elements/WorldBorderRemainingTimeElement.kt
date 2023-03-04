package net.pooleaf.gamecore.sidebar.elements

import net.pooleaf.core.modules.support.common.util.StringUtil
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.phases.WorldBorderUpdatePhase
import net.pooleaf.gamecore.sidebar.GameSideBarNamedTextElement

class WorldBorderRemainingTimeElement: GameSideBarNamedTextElement("§6경계선 축소까지") {

    override var valueText: String
        get() = (GameCore.game.phasePipeline.currentPhase as WorldBorderUpdatePhase).updateRemainingSeconds?.let { updateRemainingSeconds ->
            StringUtil.buildTimeStringFromSeconds(updateRemainingSeconds.toLong())
        } ?: ""
        set(value) {}

    override fun getPriority(): Int {
        return 10
    }

    override fun isShow(): Boolean {
        val phase = GameCore.game.phasePipeline.currentPhase
        return phase is WorldBorderUpdatePhase && phase.updateRemainingSeconds != null
    }

    override fun isUseSpace(): Boolean {
        return true
    }

}