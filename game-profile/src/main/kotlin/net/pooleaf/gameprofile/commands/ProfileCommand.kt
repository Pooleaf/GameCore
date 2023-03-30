package net.pooleaf.gameprofile.commands

import net.pooleaf.core.modules.annocommand.common.Command
import net.pooleaf.core.modules.annocommand.common.CommandResult
import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.core.modules.commonsender.common.CommonPlayer
import net.pooleaf.gameprofile.guis.GameProfileGui
import org.bukkit.entity.Player

class ProfileCommand {

    @Command(
        name = ["프로필", "profile"],
        arguments = "(닉네임)",
        description = "플레이어의 프로필을 확인합니다."
    )
    fun profile(player: CommonPlayer<Player>, result: CommandResult) {
        val targetPlayer = if (result.argumentsLength == 0) {
            player
        } else {
            CommonSenderModule.getPlayerByName(result.getArgument(0))
        }

        if (targetPlayer == null) {
            player.sendWarning("존재하지 않는 플레이어입니다.")
            return
        }

        GameProfileGui(targetPlayer).open(player.platformSender)
    }

}