package net.pooleaf.gamecore.sidebar.elements

import net.pooleaf.core.modules.support.common.util.StringUtil
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.phases.WorldBorderUpdatePhase
import net.pooleaf.gamecore.player.GamePlayer
import net.pooleaf.gamecore.sidebar.GameSideBarNamedTextElement
import net.pooleaf.gamecore.sidebar.GameSideBarPersonalNamedTextElement

class WorldBorderRemainingTimeElement: GameSideBarPersonalNamedTextElement("§6경계선 축소까지") {

    override fun getPriority(): Int {
        return 10
    }

    override fun getValueText(gamePlayer: GamePlayer): String {
        val worldBorderUpdatePhase = GameCore.game.phasePipeline.currentPhase as WorldBorderUpdatePhase
        return "${StringUtil.buildTimeStringFromSeconds(worldBorderUpdatePhase.updateRemainingSeconds?.toLong() ?: 0)} §6/ ${if (GameCore.currentMap?.isInWorldBorder(gamePlayer.player.location, worldBorderUpdatePhase.getNewWorldBorderSize()) == true) "§f안전" else "§c위험"}"
    }

    override fun isShow(gamePlayer: GamePlayer): Boolean {
        val phase = GameCore.game.phasePipeline.currentPhase
        return phase is WorldBorderUpdatePhase && phase.updateRemainingSeconds != null
    }

    override fun isUseSpace(): Boolean {
        return true
    }

}