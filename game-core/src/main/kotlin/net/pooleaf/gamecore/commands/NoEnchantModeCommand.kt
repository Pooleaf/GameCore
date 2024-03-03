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

class NoEnchantModeCommand {

    @Command(
        parent = ["", "게임"],
        name = ["노인챈트전", "노인챈전", "noEnchantMode"],
        arguments = "(true|false)",
        description = "노인챈트전 모드로 전환하거나 해제합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun game_noEnchantMode(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        if (!GameCore.game.isGameStarted) {
            sender.sendWarning("게임 중이 아닙니다.")
            return
        }

        val toggle = result.getArgumentAsBoolean(0)
        if (toggle == null) {
            result.sendUsage(sender)
            return
        }

        if (toggle) {
            if (GameCore.game.isNoEnchantMode) {
                sender.sendWarning("이미 노인챈트전으로 진행 중입니다.")
                return
            }

            GameCore.unsafe.gameManager.startNoEnchantMode()
            BukkitBroadcaster.broadcast("${sender.displayName} §b님께서 노인챈트전으로 전환시켰습니다.")
        } else {
            if (!GameCore.game.isNoEnchantMode) {
                sender.sendWarning("아직 노인챈트전이 아닙니다.")
                return
            }

            GameCore.game.isNoEnchantMode = false

            BukkitBroadcaster.broadcast("${sender.displayName} §b님께서 노인챈트전을 해제했습니다.")
            BukkitBroadcaster.broadcastSound(XSound.ENTITY_ITEM_PICKUP, 0.4F, 0.4F)
        }
    }

    @Command(
        parent = ["", "게임"],
        name = ["노인챈트전투표", "노인챈전투표", "noEnchantModeVote"],
        description = "노인챈트전 투표를 시작합니다."
    )
    fun game_noEnchantModeVote(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        if (!GameCore.gameConfig.useNoEnchantModeVote) {
            sender.sendWarning("노인챈트전 투표를 사용할 수 없습니다.")
            return
        }

        if (!GameCore.game.isGameStarted) {
            sender.sendWarning("게임 중이 아닙니다.")
            return
        }

        // 현재 Phase가 무적 Phase 전일 경우 사용 불가
        val currentPhase = GameCore.game.phasePipeline.currentPhase
        if (currentPhase != null
            && GameCore.game.phasePipeline.phases.find { it is GodModePhase } != null
            && GameCore.game.phasePipeline.phases.indexOfFirst { it == currentPhase } < GameCore.game.phasePipeline.phases.indexOfFirst { it is GodModePhase }) {
            sender.sendWarning("아직 투표할 수 없습니다.")
            return
        }

        if (GameCore.game.isNoEnchantMode) {
            sender.sendWarning("이미 노인챈트전으로 진행 중입니다.")
            return
        }

        if (GameCore.unsafe.noEnchantModeVoteManager.isVoteStarted) {
            sender.sendWarning("이미 노인챈트전 투표가 시작되었습니다.")
            return
        }

        GameCore.unsafe.noEnchantModeVoteManager.startVote()
    }

    @Command(
        parent = ["노인챈트전투표", "게임 노인챈트전투표"],
        name = ["찬성", "agree"],
        description = "노인챈트전 투표에 찬성합니다."
    )
    fun game_noEnchantModeVote_agree(player: Player, result: CommandResult) {
        if (!GameCore.gameConfig.useNoEnchantModeVote) {
            player.sendWarning("노인챈트전 투표를 사용할 수 없습니다.")
            return
        }

        if (!GameCore.game.isGameStarted) {
            player.sendWarning("게임 중이 아닙니다.")
            return
        }

        val gamePlayer = player.toGamePlayer() ?: return
        GameCore.unsafe.noEnchantModeVoteManager.voteToAgree(gamePlayer)
    }

    @Command(
        parent = ["노인챈트전투표", "게임 노인챈트전투표"],
        name = ["반대", "disagree"],
        description = "노인챈트전 투표에 반대합니다."
    )
    fun game_noEnchantModeVote_disagree(player: Player, result: CommandResult) {
        if (!GameCore.gameConfig.useNoEnchantModeVote) {
            player.sendWarning("노인챈트전 투표를 사용할 수 없습니다.")
            return
        }

        if (!GameCore.game.isGameStarted) {
            player.sendWarning("게임 중이 아닙니다.")
            return
        }

        val gamePlayer = player.toGamePlayer() ?: return
        GameCore.unsafe.noEnchantModeVoteManager.voteToDisagree(gamePlayer)
    }

}