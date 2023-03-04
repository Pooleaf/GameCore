package net.pooleaf.gamecore.vote.map

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.map.GameMap
import net.pooleaf.gamecore.player.GamePlayer


class MapVoteManager {

    val mapVote = MapVote()
    val mapVoteGui = MapVoteGui()


    fun initVote() {
        mapVote.clear()
        mapVoteGui.updateAsynchronously()
    }

    fun voteTo(gamePlayer: GamePlayer, map: GameMap) {
        if (GameCore.game.isRunning) {
            gamePlayer.sendWarningSafely("이미 게임이 시작되었습니다.")
            return
        }

        if (gamePlayer.isSpectator) {
            gamePlayer.sendWarningSafely("관전 중에는 투표에 참여할 수 없습니다.")
            return
        }

        if (mapVote.votedMap.get(gamePlayer.uuid)?.let { it == map } == true) {
            gamePlayer.sendWarningSafely("이미 ${map.displayName} 맵에 투표했습니다.")
            return
        }

        mapVote.voteTo(gamePlayer.uuid, map)
        mapVoteGui.updateAsynchronously()
        GameCore.unsafe.quickBarManager.waitingQuickBar.update()
        GameCore.unsafe.mapManager.currentMap = mapVote.getMostVotedMap()

        gamePlayer.sendMessageSafely("${map.displayName} §e맵에 투표했습니다.")
    }

    fun voteToRandom(gamePlayer: GamePlayer) {
        if (!mapVote.votedMap.containsKey(gamePlayer.uuid)) {
            gamePlayer.sendWarningSafely("이미 랜덤에 투표했습니다.")
            return
        }

        mapVote.unvote(gamePlayer.uuid)
        mapVoteGui.updateAsynchronously()
        GameCore.unsafe.quickBarManager.waitingQuickBar.update()

        gamePlayer.sendMessageSafely("랜덤§e에 투표했습니다.")
    }

    fun unvote(gamePlayer: GamePlayer) {
        mapVote.unvote(gamePlayer.uuid)
        mapVoteGui.updateAsynchronously()
        GameCore.unsafe.quickBarManager.waitingQuickBar.update()
    }

}