package net.pooleaf.gamecore.team

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import net.pooleaf.core.modules.support.common.manager.AbstractManager
import net.pooleaf.gamecore.player.GamePlayer
import java.util.*


class TeamNameTagManager : AbstractManager<UUID, String>() { // PlayerUUID, TeamName

    /**
     * 팀 이름표 접두사를 보여줍니다.
     */
    fun setTeamNameTag(gamePlayer: GamePlayer) {
        if (!gamePlayer.isOnline) return
        if (gamePlayer.team == null) return

        // 이미 팀을 생성했을 경우 삭제 후 다시생성
        if (exists(gamePlayer.uuid)) {
            removeTeamNameTag(gamePlayer)
        }

        val teamName = UUID.randomUUID().toString().replace("-", "").substring(0, 12)

        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SCOREBOARD_TEAM)
        packet.strings.write(0, teamName)
        packet.integers.write(1, 0)
        packet.strings.write(2, "§7[팀] §f")
        packet.getSpecificModifier(Collection::class.java).write(0, gamePlayer.team!!.players.map { it.name })

        ProtocolLibrary.getProtocolManager().sendServerPacket(gamePlayer.player, packet)

        set(gamePlayer.uuid, teamName)
    }

    /**
     * 팀 이름표 접두사를 삭제합니다.
     */
    fun removeTeamNameTag(gamePlayer: GamePlayer) {
        if (!gamePlayer.isOnline) return

        val teamName = get(gamePlayer.uuid) ?: return

        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SCOREBOARD_TEAM)
        packet.strings.write(0, teamName)
        packet.integers.write(1, 1)

        ProtocolLibrary.getProtocolManager().sendServerPacket(gamePlayer.player, packet)
    }

}