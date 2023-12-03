package net.pooleaf.gamecore.vote.godmodeskip

import com.cryptomorin.xseries.XSound
import net.pooleaf.core.modules.support.bukkit.util.BukkitBroadcaster
import net.pooleaf.core.modules.support.common.component.SimpleComponentBuilder
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.phases.GodModePhase
import net.pooleaf.gamecore.player.GamePlayer


class GodModeSkipVoteManager {

    var isGodModeSkipVoteStarted = false
        private set

    val godModeSkipVote = GodModeSkipVote()


    private fun isGodModePhase(): Boolean {
        val currentPhase = GameCore.game.phasePipeline.currentPhase
        return !(currentPhase == null || currentPhase !is GodModePhase || !currentPhase.isStarted || currentPhase.isEnded)
    }

    /**
     * 투표를 초기화합니다.
     */
    fun initVote() {
        isGodModeSkipVoteStarted = false
        godModeSkipVote.clear()
    }

    /**
     * 투표를 시작합니다.
     */
    fun startVote() {
        if (isGodModeSkipVoteStarted) return
        if (!isGodModePhase()) return

        val agreeComponent = SimpleComponentBuilder("§2§l[ 찬성 ]")
            .clickRunCommand("/무적해제투표 찬성")
            .hoverShowText("§a클릭 시 §f무적 해제 투표§a에 찬성합니다. §7(/무적해제투표 찬성)")
            .build()
        val disagreeComponent = SimpleComponentBuilder(" §c§l[ 반대 ]")
            .clickRunCommand("/무적해제투표 반대")
            .hoverShowText("§c클릭 시 §f무적 해제 투표§c에 반대합니다. §7(/무적해제투표 반대)")
            .build()

        agreeComponent.addExtra(disagreeComponent)

        BukkitBroadcaster.broadcast("")
        BukkitBroadcaster.broadcast("§e무적 해제 투표가 시작되었습니다.")
        BukkitBroadcaster.broadcast("§e과반수 이상이 투표에 찬성할 경우 무적이 해제됩니다.")
        BukkitBroadcaster.broadcast(agreeComponent)
        BukkitBroadcaster.broadcastSound(XSound.ENTITY_ITEM_PICKUP, 0.4F, 0.4F)

        isGodModeSkipVoteStarted = true
    }

    /**
     * 플레이어를 무적 해제 투표에 찬성시킵니다.
     */
    fun voteToAgree(gamePlayer: GamePlayer) {
        if (!isGodModePhase()) {
            gamePlayer.sendWarningSafely("무적 시간이 아닙니다.")
            return
        }

        if (!isGodModeSkipVoteStarted) {
            startVote()
        }

        if (gamePlayer.isSpectator) {
            gamePlayer.sendWarningSafely("관전 중에는 투표에 참여할 수 없습니다.")
            return
        }
        if (godModeSkipVote.isVoted(gamePlayer.uuid)) {
            gamePlayer.sendWarningSafely("이미 투표했습니다.")
            return
        }
        if (godModeSkipVote.isAgree(gamePlayer.uuid)) {
            gamePlayer.sendWarningSafely("이미 투표에 찬성했습니다.")
            return
        }

        godModeSkipVote.voteToAgree(gamePlayer.uuid)

        gamePlayer.sendMessageSafely("§a무적 해제 투표에 §l찬성§a했습니다.")
        broadcastProgress()

        // 과반수 동의 시 무적해제
        // 최소 2명 투표해야 무적해제됨
        if (isGodModePhase()
            && godModeSkipVote.agreePlayers.size >= 2
            && godModeSkipVote.agreePlayers.size >= GameCore.unsafe.playerManager.getOnlineJoinedPlayers().size.toFloat() / 2) {

            GameCore.game.phasePipeline.currentPhase?.end()
        }
    }

    /**
     * 플레이어를 무적 해제 투표에 반대시킵니다.
     */
    fun voteToDisagree(gamePlayer: GamePlayer) {
        if (!isGodModePhase()) {
            gamePlayer.sendWarningSafely("무적 시간이 아닙니다.")
            return
        }

        if (!isGodModeSkipVoteStarted) {
            startVote()
        }

        if (gamePlayer.isSpectator) {
            gamePlayer.sendWarningSafely("관전 중에는 투표에 참여할 수 없습니다.")
            return
        }
        if (godModeSkipVote.isVoted(gamePlayer.uuid)) {
            gamePlayer.sendWarningSafely("이미 투표했습니다.")
            return
        }
        if (godModeSkipVote.isDisagree(gamePlayer.uuid)) {
            gamePlayer.sendWarningSafely("이미 투표에 반대했습니다.")
            return
        }

        godModeSkipVote.voteToDisagree(gamePlayer.uuid)

        gamePlayer.sendMessageSafely("§c무적 해제 투표에 §l반대§c했습니다.")
        broadcastProgress()
    }

    /**
     * 플레이어의 무적 해제 투표를 취소합니다.
     */
    fun unvote(gamePlayer: GamePlayer) {
        godModeSkipVote.unvote(gamePlayer.uuid)
    }

    private fun broadcastProgress() {
        val agreeCount = godModeSkipVote.agreePlayers.size
        val disagreeCount = godModeSkipVote.disagreePlayers.size

        BukkitBroadcaster.broadcast("§e무적 해제 투표를 진행 중입니다.. §a찬성: ${agreeCount}§a명 §c반대: ${disagreeCount}§c명")
    }

}