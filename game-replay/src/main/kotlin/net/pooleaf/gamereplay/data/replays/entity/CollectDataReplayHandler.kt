package net.pooleaf.gamereplay.data.replays.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.cryptomorin.xseries.XSound
import net.pooleaf.core.modules.support.bukkit.sound.play
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.entity.CollectData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import net.pooleaf.gamereplay.replay.ReplayPlayer
import org.bukkit.entity.Player
import kotlin.random.Random

class CollectDataReplayHandler : RecordDataReplayHandler<CollectData> {

    override fun onPlay(recordData: CollectData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)
        val citizensNpc = replayPlayer.virtualPlayerManager.get(recordData.collectorPlayerUuid)?.citizensNpc ?: return
        if (citizensNpc.entity == null) return

        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.COLLECT)
        packet.integers.write(0, recordData.collectedEntityId + ReplayPlayer.ENTITY_ID_OFFSET)
        packet.integers.write(1, citizensNpc.entity.entityId)
        ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, packet)

        XSound.ENTITY_ITEM_PICKUP.play(viewer, citizensNpc.entity.location, 0.2F, ((Random.nextFloat() - Random.nextFloat()) * 0.7F + 1.0F) * 2.0F)
    }

}