package net.pooleaf.gamecore.sidebar

import net.pooleaf.gamecore.player.GamePlayer

abstract class GameSideBarPersonalNamedTextElement (
    var nameText: String
): GameSideBarPersonalElement() {

    abstract fun getValueText(gamePlayer: GamePlayer): String

    override fun getTexts(gamePlayer: GamePlayer): List<String> {
        return listOf(nameText, getValueText(gamePlayer))
    }

}