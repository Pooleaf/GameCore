package net.pooleaf.gamecore.vote.map

import net.pooleaf.gamecore.map.GameMap
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class MapVote {

    val votedMap = ConcurrentHashMap<UUID, GameMap>()


    /**
     * 플레이어를 [map]에 투표시킵니다.
     */
    fun voteTo(playerUuid: UUID, map: GameMap) {
        votedMap.put(playerUuid, map)
    }

    /**
     * 플레이어의 투표를 취소시킵니다.
     */
    fun unvote(playerUuid: UUID) {
        votedMap.remove(playerUuid)
    }

    /**
     * 모든 투표를 취소합니다.
     */
    fun clear() {
        votedMap.clear()
    }

    /**
     * [map]의 투표 수를 확인합니다.
     */
    fun getVoteCount(map: GameMap): Int {
        return votedMap.filter { it.value == map }.count()
    }

    /**
     * 모든 투표 수를 확인합니다.
     */
    fun getVoteCount(): Int {
        return votedMap.size
    }

    /**
     * 가장 투표를 많이 받은 맵을 반환합니다.
     * 만약 모든 맵의 투표 수가 0이라면 null을 반환합니다.
     */
    fun getMostVotedMap(): GameMap? {
        val voteCounts = HashMap<GameMap, Int>()

        votedMap.values.forEach { map ->
            voteCounts.put(map, voteCounts.getOrDefault(map, 0) + 1)
        }

        val mapPair = voteCounts.maxByOrNull { it.value } ?: return null

        if (mapPair.value == 0) {
            return null
        }

        return mapPair.key
    }

}