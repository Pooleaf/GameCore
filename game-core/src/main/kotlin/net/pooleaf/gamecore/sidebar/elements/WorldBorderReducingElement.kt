package net.pooleaf.gamecore.sidebar.elements

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.phases.WorldBorderUpdatePhase
import net.pooleaf.gamecore.player.GamePlayer
import net.pooleaf.gamecore.sidebar.GameSideBarElement
import net.pooleaf.gamecore.sidebar.GameSideBarPersonalNamedTextElement

class WorldBorderReducingElement: GameSideBarPersonalNamedTextElement("§6경계선 축소 중..") {

    override fun getPriority(): Int {
        return 10
    }

    override fun getValueText(gamePlayer: GamePlayer): String {
        return if (GameCore.currentMap?.isInWorldBorder(gamePlayer.player.location) == true) "안전" else "§c위험"
    }

    override fun isShow(gamePlayer: GamePlayer): Boolean {
        val phase = GameCore.game.phasePipeline.currentPhase
        return phase is WorldBorderUpdatePhase && phase.updateDurationSeconds != null
    }

    override fun isUseSpace(): Boolean {
        return true
    }

}