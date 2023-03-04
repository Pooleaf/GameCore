package net.pooleaf.gamecore.sidebar

abstract class GameSideBarNamedTextElement(
    open var nameText: String,
    open var valueText: String = "",
): GameSideBarElement {

    final override fun getTexts(): List<String> {
        return listOf(nameText, valueText)
    }

}