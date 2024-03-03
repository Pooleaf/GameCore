package net.pooleaf.gamecore.vote.noenchantmode

import com.cryptomorin.xseries.XSound
import net.pooleaf.core.modules.support.bukkit.util.BukkitBroadcaster
import net.pooleaf.core.modules.support.common.component.SimpleComponentBuilder
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.player.GamePlayer


class NoEnchantModeVoteManager {

    var isVoteStarted = false
        private set

    val noEnchantModeVote = NoEnchantModeVote()


    /**
     * 투표를 초기화합니다.
     */
    fun initVote() {
        isVoteStarted = false
        noEnchantModeVote.clear()
    }

    /**
     * 투표를 시작합니다.
     */
    fun startVote() {
        if (isVoteStarted) return
        if (!GameCore.gameConfig.useNoEnchantModeVote) return

        val agreeComponent = SimpleComponentBuilder("§2§l[ 찬성 ]")
            .clickRunCommand("/노인챈트전투표 찬성")
            .hoverShowText("§a클릭 시 §f노인챈트전 투표§a에 찬성합니다. §7(/노인챈트전투표 찬성)")
            .build()
        val disagreeComponent = SimpleComponentBuilder(" §c§l[ 반대 ]")
            .clickRunCommand("/노인챈트전투표 반대")
            .hoverShowText("§c클릭 시 §f노인챈트전 투표§c에 반대합니다. §7(/노인챈트전투표 반대)")
            .build()

        agreeComponent.addExtra(disagreeComponent)

        BukkitBroadcaster.broadcast("")
        BukkitBroadcaster.broadcast("§e노인챈트전 투표가 시작되었습니다.")
        if (GameCore.game.isGodMode) {
            BukkitBroadcaster.broadcast("§e과반수 이상이 투표에 찬성할 경우 §6§l모든 인챈트 아이템을 사용할 수 없게§e되고, §6§l무적이 해제§e됩니다.")
        } else {
            BukkitBroadcaster.broadcast("§e과반수 이상이 투표에 찬성할 경우 §6§l모든 인챈트 아이템을 사용할 수 없게§e됩니다.")
        }
        BukkitBroadcaster.broadcast(agreeComponent)
        BukkitBroadcaster.broadcastSound(XSound.ENTITY_ITEM_PICKUP, 0.4F, 0.4F)
    }

    /**
     * 플레이어를 무적 해제 투표에 찬성시킵니다.
     */
    fun voteToAgree(gamePlayer: GamePlayer) {
        if (!GameCore.game.isGameStarted) {
            gamePlayer.sendWarningSafely("게임 중이 아닙니다.")
            return
        }

        if (GameCore.game.isNoEnchantMode) {
            gamePlayer.sendWarningSafely("이미 노인챈트전으로 진행 중입니다.")
            return
        }

        if (gamePlayer.isSpectator) {
            gamePlayer.sendWarningSafely("관전 중에는 투표에 참여할 수 없습니다.")
            return
        }
        if (noEnchantModeVote.isVoted(gamePlayer.uuid)) {
            gamePlayer.sendWarningSafely("이미 투표했습니다.")
            return
        }

        if (!isVoteStarted) {
            startVote()
        }

        noEnchantModeVote.voteToAgree(gamePlayer.uuid)

        gamePlayer.sendMessageSafely("§a노인챈트전 투표에 §l찬성§a했습니다.")
        broadcastProgress()

        // 과반수 동의 시 노인챈트전 시작
        // 최소 2명 투표해야 노인챈트전 시작됨
        if (!GameCore.game.isNoEnchantMode
            && noEnchantModeVote.agreePlayers.size >= 2
            && noEnchantModeVote.agreePlayers.size >= GameCore.unsafe.playerManager.getOnlineJoinedPlayers().size.toFloat() / 2) {

            GameCore.unsafe.gameManager.startNoEnchantMode()
        }
    }

    /**
     * 플레이어를 무적 해제 투표에 반대시킵니다.
     */
    fun voteToDisagree(gamePlayer: GamePlayer) {
        if (!GameCore.game.isGameStarted) {
            gamePlayer.sendWarningSafely("게임 중이 아닙니다.")
            return
        }

        if (GameCore.game.isNoEnchantMode) {
            gamePlayer.sendWarningSafely("이미 노인챈트전으로 진행 중입니다.")
            return
        }

        if (gamePlayer.isSpectator) {
            gamePlayer.sendWarningSafely("관전 중에는 투표에 참여할 수 없습니다.")
            return
        }

        if (noEnchantModeVote.isVoted(gamePlayer.uuid)) {
            gamePlayer.sendWarningSafely("이미 투표했습니다.")
            return
        }

        if (!isVoteStarted) {
            startVote()
        }

        noEnchantModeVote.voteToDisagree(gamePlayer.uuid)

        gamePlayer.sendMessageSafely("§c노인챈트전 투표에 §l반대§c했습니다.")
        broadcastProgress()
    }

    /**
     * 노인챈트전 투표를 취소합니다.
     */
    fun unvote(gamePlayer: GamePlayer) {
        noEnchantModeVote.unvote(gamePlayer.uuid)
    }

    private fun broadcastProgress() {
        val agreeCount = noEnchantModeVote.agreePlayers.size
        val disagreeCount = noEnchantModeVote.disagreePlayers.size

        BukkitBroadcaster.broadcast("§e노인챈트전 투표를 진행 중입니다.. §a찬성: ${agreeCount}§a명 §c반대: ${disagreeCount}§c명")
    }

}