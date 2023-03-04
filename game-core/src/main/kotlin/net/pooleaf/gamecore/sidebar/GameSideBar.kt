package net.pooleaf.gamecore.sidebar

import kotlinx.coroutines.launch
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.gui.bukkit.sidebar.SideBar
import net.pooleaf.gamecore.player.GamePlayer
import org.bukkit.Bukkit
import java.util.concurrent.ConcurrentHashMap

open class GameSideBar(val title: String) {

    private var commonSideBar: SideBar? = null

    val elements = arrayListOf<GameSideBarElement>()

    val viewers = ConcurrentHashMap<GamePlayer, SideBar>()


    val isPersonalSideBar: Boolean
        get() = elements.filterIsInstance<GameSideBarPersonalElement>().isNotEmpty()


    /**
     * 모든 사이드바를 업데이트합니다.
     */
    fun update() {
        // 공용
        if (!isPersonalSideBar) {
            if (commonSideBar != null) {
                commonSideBar = SideBar(title)
            }

            commonSideBar?.texts = elements.filter { it.isShow() }
                .sortedBy { it.getPriority() }
                .flatMap { it.getTexts() }
                .toList()

            BukkitSyncScope.launch {
                viewers.forEach { (gamePlayer, _) ->
                    gamePlayer.player.scoreboard = commonSideBar?.updateScoreboard(null)
                }
            }
        }
        // 개인용
        else {
            viewers.forEach { (gamePlayer, _) ->
                update(gamePlayer)
            }
        }
    }

    /**
     * 플레이어의 개인 사이드바를 업데이트합니다.
     */
    fun update(gamePlayer: GamePlayer) {
        if (!isPersonalSideBar) error("Personal element is not exists")
        if (!gamePlayer.isOnline) error("gamePlayer is not online")

        if (!viewers.containsKey(gamePlayer)) {
            viewers.put(gamePlayer, SideBar(title))
        }

        // Element 정렬
        val sortedElements = elements.filter {
            if (it is GameSideBarPersonalElement) {
                it.isShow(gamePlayer)
            } else {
                it.isShow()
            }
        }.sortedBy { it.getPriority() }

        // 텍스트 생성
        val texts = arrayListOf<String>()
        var beforeElement: GameSideBarElement? = null
        var spaceCount = 0
        sortedElements.forEach { element ->
            // 한칸 띄기
            if (beforeElement != null && beforeElement!!.isUseSpace() && element.isUseSpace()) {
                texts.add("§f".repeat(++spaceCount))
            }

            if (element is GameSideBarPersonalElement) {
                texts.addAll(element.getTexts(gamePlayer))
            } else {
                texts.addAll(element.getTexts())
            }

            beforeElement = element
        }


        val sideBar = viewers.get(gamePlayer)!!
        sideBar.texts = texts

        // 적용
        BukkitSyncScope.launch {
            gamePlayer.player.scoreboard = sideBar.updateScoreboard(null)
        }
    }

    /**
     * 플레이어에게 사이드바를 보여줍니다.
     */
    fun setTo(gamePlayer: GamePlayer) {
        if (viewers.containsKey(gamePlayer)) error("gamePlayer is already use sidebar")
        if (!gamePlayer.isOnline) error("gamePlayer is not online")

        viewers.put(gamePlayer, SideBar(title))

        // 공용
        if (!isPersonalSideBar) {
            BukkitSyncScope.launch {
                commonSideBar?.updateScoreboard(gamePlayer.player.scoreboard)
            }
        }
        // 개인용
        else {
            update(gamePlayer)
        }
    }

    /**
     * 플레이어에게서 사이드바를 제거합니다.
     */
    fun removeTo(gamePlayer: GamePlayer) {
        if (!viewers.containsKey(gamePlayer)) error("gamePlayer is already not use sidebar")
        if (!gamePlayer.isOnline) error("gamePlayer is not online")

        viewers.remove(gamePlayer)
        gamePlayer.player.scoreboard = Bukkit.getScoreboardManager().newScoreboard
    }

}