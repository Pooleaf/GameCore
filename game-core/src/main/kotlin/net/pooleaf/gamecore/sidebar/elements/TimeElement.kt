package net.pooleaf.gamecore.sidebar.elements

import net.pooleaf.gamecore.sidebar.GameSideBarElement
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TimeElement: GameSideBarElement {

    override fun getPriority(): Int {
        return 0
    }

    override fun getTexts(): List<String> {
        val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("a hh:mm"))
        val date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd"))
        val text = "§7${time} §8${date}"

        return listOf(text)
    }

    override fun isShow(): Boolean {
        return true
    }

    override fun isUseSpace(): Boolean {
        return false
    }

}