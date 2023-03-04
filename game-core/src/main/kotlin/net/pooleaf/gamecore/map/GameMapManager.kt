package net.pooleaf.gamecore.map

import net.pooleaf.core.modules.support.common.manager.AbstractSyncManager

/**
 * [GameMap] 객체를 관리합니다.
 * [T]를 통해 커스텀 [GameMap] 객체를 관리할 수 있습니다.
 */
open class GameMapManager<T: GameMap>(
    val gameMapFactory: GameMapFactory<T>
): AbstractSyncManager<String, T>() {

    // 현재 맵
    var currentMap: GameMap? = null


    /**
     * 무작위 맵을 반환합니다.
     */
    fun getRandomMap(): T {
        return values().random()
    }

    /**
     * 사용 가능한 무작위 맵을 반환합니다.
     */
    fun getRandomMapCanUse(): T {
        return values().filter { it.canUse }.random()
    }

}