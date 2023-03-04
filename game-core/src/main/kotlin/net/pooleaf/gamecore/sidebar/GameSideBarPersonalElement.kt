package net.pooleaf.gamecore.sidebar

import net.pooleaf.gamecore.player.GamePlayer

abstract class GameSideBarPersonalElement: GameSideBarElement {

    abstract fun getTexts(gamePlayer: GamePlayer): List<String>

    abstract fun isShow(gamePlayer: GamePlayer): Boolean

    final override fun getTexts(): List<String> {
        error("getTexts() is not support")
    }

    final override fun isShow(): Boolean {
        error("isShow() is not supported")
    }

}