package net.pooleaf.gamecore.sidebar

interface GameSideBarElement {

    /**
     * 우선순위를 반환합니다.
     * 우선순위가 낮을 수록 상단에 배치됩니다.
     */
    fun getPriority(): Int

    /**
     * 사이드바에 표시될 텍스트를 반환합니다.
     */
    fun getTexts(): List<String>

    /**
     * 사이드바에 보여질지 반환합니다.
     */
    fun isShow(): Boolean

    /**
     * 다른 Element와 붙어있을 때 공간을 둘지를 반환합니다.
     * 다른 Element도 isUseSpace()가 true여야 합니다.
     */
    fun isUseSpace(): Boolean

}