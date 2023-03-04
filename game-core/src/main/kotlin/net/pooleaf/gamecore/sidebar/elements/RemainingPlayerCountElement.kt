package net.pooleaf.gamecore.sidebar.elements

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.sidebar.GameSideBarNamedTextElement

class RemainingPlayerCountElement: GameSideBarNamedTextElement("§2남은 인원") {

    override var valueText: String
        get() = "${GameCore.unsafe.playerManager.getOnlinePlayingPlayers().size}명"
        set(value) {}

    override fun getPriority(): Int {
        return 90
    }

    override fun isShow(): Boolean {
        return GameCore.game.isGameStarted
    }

    override fun isUseSpace(): Boolean {
        return true
    }

}