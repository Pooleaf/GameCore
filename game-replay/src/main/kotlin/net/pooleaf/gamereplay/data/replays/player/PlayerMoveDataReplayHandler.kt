package net.pooleaf.gamereplay.data.replays.player

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.player.PlayerMoveData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent

class PlayerMoveDataReplayHandler : RecordDataReplayHandler<PlayerMoveData> {

    override fun onPlay(recordData: PlayerMoveData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)
        val citizensNpc = replayPlayer.virtualPlayerManager.get(recordData.playerUuid)?.citizensNpc ?: return

        val location = Location(Bukkit.getWorld(recordData.worldName), recordData.x, recordData.y, recordData.z, recordData.yaw, recordData.pitch)
        citizensNpc.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN)
    }

}