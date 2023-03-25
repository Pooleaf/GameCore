package net.pooleaf.gamereplay.data.replays.player

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.player.PlayerTeleportData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

class PlayerTeleportDataReplayHandler : RecordDataReplayHandler<PlayerTeleportData> {

    override fun onPlay(recordData: PlayerTeleportData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)
        val virtualPlayer = replayPlayer.virtualPlayerManager.get(recordData.playerUuid)

        val location = Location(Bukkit.getWorld(recordData.worldName), recordData.x, recordData.y, recordData.z, recordData.yaw, recordData.pitch)
        virtualPlayer.teleport(viewer, location)
    }

}