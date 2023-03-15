package net.pooleaf.gamereplay.commands

import net.pooleaf.core.modules.annocommand.common.Command
import net.pooleaf.core.modules.annocommand.common.CommandResult
import net.pooleaf.core.modules.commonsender.common.CommonPlayer
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.GameReplayPermission
import org.bukkit.entity.Player

class AdminReplayCommand {

    @Command(
        parent = ["리플레이관리"],
        name = ["스폰설정", "setSpawn"],
        description = "현재 위치 스폰 위치로 설정합니다.",
        color = CommonChatColor.AQUA,
        permission = GameReplayPermission.ADMIN
    )
    fun replay_setSpawn(player: CommonPlayer<Player>, result: CommandResult) {
        GameReplayApi.spawnConfig.spawnLocation = player.platformSender.location
        GameReplayApi.spawnConfig.save()

        player.sendMessage("§b현재 위치를 스폰 위치로 설정했습니다.")
    }

}