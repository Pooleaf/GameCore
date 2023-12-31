package net.pooleaf.gamereplay.data.replays.player

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.cryptomorin.xseries.XSound
import net.pooleaf.core.modules.support.bukkit.sound.play
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.player.PlayerDamageData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player

class PlayerDamageDataReplayHandler : RecordDataReplayHandler<PlayerDamageData> {

    override fun onPlay(recordData: PlayerDamageData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val citizensNpc = replayPlayer.virtualPlayerManager.get(recordData.playerUuid)?.citizensNpc ?: return
        if (citizensNpc.entity == null) return

        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ANIMATION)
        packet.integers.write(0, citizensNpc.entity.entityId)
        packet.integers.write(1, 1)
        ProtocolLibrary.getProtocolManager().sendServerPacket(replayPlayer.viewer, packet)

        XSound.ENTITY_PLAYER_HURT.play(replayPlayer.viewer, citizensNpc.entity.location, 0.8F, 1.0F)
    }

}