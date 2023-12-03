package net.pooleaf.gamecore.commands

import com.cryptomorin.xseries.XSound
import net.pooleaf.core.modules.annocommand.common.Command
import net.pooleaf.core.modules.annocommand.common.CommandResult
import net.pooleaf.core.modules.commonsender.common.CommonCommandSender
import net.pooleaf.core.modules.support.bukkit.messager.sendWarning
import net.pooleaf.core.modules.support.bukkit.util.BukkitBroadcaster
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.GameCorePermission
import net.pooleaf.gamecore.phases.GodModePhase
import net.pooleaf.gamecore.utils.toGamePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GodModeSkipCommand {

    private fun isGodModePhase(): Boolean {
        val currentPhase = GameCore.game.phasePipeline.currentPhase
        return !(currentPhase == null || currentPhase !is GodModePhase || !currentPhase.isStarted || currentPhase.isEnded)
    }

    @Command(
        parent = ["", "게임"],
        name = ["무적해제", "무적스킵", "skipGodMode", "go"],
        description = "무적을 해제시킵니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun game_skipGodMode(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        if (!isGodModePhase()) {
            sender.sendWarning("무적 시간이 아닙니다.")
            return
        }

        GameCore.game.phasePipeline.currentPhase?.end()

        BukkitBroadcaster.broadcast("${sender.displayName} §b님께서 무적 시간을 종료시켰습니다.")
        BukkitBroadcaster.broadcastSound(XSound.ENTITY_ITEM_PICKUP, 0.4F, 0.4F)
    }

    @Command(
        parent = ["", "게임"],
        name = ["무적해제투표", "godModeSkipVote"],
        description = "무적 해제 투표를 시작합니다."
    )
    fun game_godModeSkipVote(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        if (!isGodModePhase()) {
            sender.sendWarning("무적 시간이 아닙니다.")
            return
        }
        if (GameCore.unsafe.godModeSkipVoteManager.isGodModeSkipVoteStarted) {
            sender.sendWarning("이미 무적 해제 투표가 시작되었습니다.")
            return
        }

        GameCore.unsafe.godModeSkipVoteManager.startVote()
    }

    @Command(
        parent = ["무적해제투표", "게임 무적해제투표"],
        name = ["찬성", "agree"],
        description = "무적 해제 투표에 찬성합니다."
    )
    fun game_godModeSkipVote_agree(player: Player, result: CommandResult) {
        if (!isGodModePhase()) {
            player.sendWarning("무적 시간이 아닙니다.")
            return
        }

        val gamePlayer = player.toGamePlayer() ?: return
        GameCore.unsafe.godModeSkipVoteManager.voteToAgree(gamePlayer)
    }

    @Command(
        parent = ["무적해제투표", "게임 무적해제투표"],
        name = ["반대", "disagree"],
        description = "무적 해제 투표에 반대합니다."
    )
    fun game_godModeSkipVote_disagree(player: Player, result: CommandResult) {
        if (!isGodModePhase()) {
            player.sendWarning("무적 시간이 아닙니다.")
            return
        }

        val gamePlayer = player.toGamePlayer() ?: return
        GameCore.unsafe.godModeSkipVoteManager.voteToDisagree(gamePlayer)
    }

}