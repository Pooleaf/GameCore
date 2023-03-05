package net.pooleaf.gamecore.vote.start

import kotlinx.coroutines.launch
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.support.bukkit.util.BukkitBroadcaster
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.player.GamePlayer


class StartVoteManager {

    val startVote = StartVote()
    val startVoteGui = StartVoteGui()


    /**
     * 투표를 초기화합니다.
     */
    fun initVote() {
        startVote.clear()
        startVoteGui.updateAsynchronously()
    }

    /**
     * 플레이어를 시작 투표에 찬성시킵니다.
     */
    fun voteToAgree(gamePlayer: GamePlayer) {
        if (GameCore.game.isRunning) {
            gamePlayer.sendWarningSafely("이미 게임이 시작되었습니다.")
            return
        }

        if (gamePlayer.isSpectator) {
            gamePlayer.sendWarningSafely("관전 중에는 투표에 참여할 수 없습니다.")
            return
        }

        if (startVote.isAgree(gamePlayer.uuid)) {
            gamePlayer.sendWarningSafely("이미 투표에 찬성했습니다.")
            return
        }

        startVote.voteToAgree(gamePlayer.uuid)
        startVoteGui.updateAsynchronously()

        gamePlayer.sendMessageSafely("§a시작 투표에 §l찬성§a했습니다.")

        broadcastProgress()

        // 과반수 동의 시 게임 시작
        // 최소 2명 투표해야 시작 가능
        if (!GameCore.game.isCountingStarted
            && startVote.agreePlayers.size >= 2
            && startVote.agreePlayers.size >= GameCore.unsafe.playerManager.getOnlineJoinedPlayers().size.toFloat() / 2) {
            BukkitSyncScope.launch { GameCore.unsafe.gameManager.startGame(null) }
        }
    }

    /**
     * 플레이어를 시작 투표에 반대시킵니다.
     */
    fun voteToDisagree(gamePlayer: GamePlayer) {
        if (GameCore.game.isRunning) {
            gamePlayer.sendWarningSafely("이미 게임이 시작되었습니다.")
            return
        }

        if (gamePlayer.isSpectator) {
            gamePlayer.sendWarningSafely("관전 중에는 투표에 참여할 수 없습니다.")
            return
        }

        if (startVote.isDisagree(gamePlayer.uuid)) {
            gamePlayer.sendWarningSafely("이미 투표에 반대했습니다.")
            return
        }

        startVote.voteToDisagree(gamePlayer.uuid)
        startVoteGui.updateAsynchronously()

        gamePlayer.sendMessageSafely("§c시작 투표에 §l반대§c했습니다.")
        broadcastProgress()
    }

    /**
     * 플레이어의 시작 투표를 취소합니다.
     */
    fun unvote(gamePlayer: GamePlayer) {
        startVote.unvote(gamePlayer.uuid)
        startVoteGui.updateAsynchronously()
    }

    private fun broadcastProgress() {
        val agreeCount = startVote.agreePlayers.size
        val disagreeCount = startVote.disagreePlayers.size

        BukkitBroadcaster.broadcast("§e게임 시작 투표를 진행 중입니다.. §a찬성: ${agreeCount}§a명 §c반대: ${disagreeCount}§c명")
    }

}