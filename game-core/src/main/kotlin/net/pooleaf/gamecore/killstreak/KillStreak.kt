package net.pooleaf.gamecore.killstreak

import net.pooleaf.core.modules.support.common.CommonChatColor

enum class KillStreak(
    val text: String,
    val color: CommonChatColor
) {

    DOUBLE("더블킬", CommonChatColor.RED),
    TRIPLE("트리플킬", CommonChatColor.RED),
    QUADRA("쿼드라킬", CommonChatColor.DARK_RED),
    PENTA("펜타킬", CommonChatColor.DARK_PURPLE)
    ;

    fun getNextKillStreak(): KillStreak? {
        val currentIndex = values().indexOf(this)

        // 마지막일 경우 null 반환
        return if (values().size == currentIndex + 1) {
            null
        } else {
            values()[currentIndex + 1]
        }
    }

}