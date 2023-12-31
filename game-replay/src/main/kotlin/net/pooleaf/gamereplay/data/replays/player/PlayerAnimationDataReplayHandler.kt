package net.pooleaf.gamereplay.data.replays.player

import net.citizensnpcs.util.PlayerAnimation
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.player.PlayerAnimationData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerAnimationType

class PlayerAnimationDataReplayHandler : RecordDataReplayHandler<PlayerAnimationData> {

    override fun onPlay(recordData: PlayerAnimationData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val citizensNpc = replayPlayer.virtualPlayerManager.get(recordData.playerUuid)?.citizensNpc ?: return
        if (citizensNpc.entity == null) return

        val animationType = PlayerAnimationType.valueOf(recordData.animationType!!)
        if (animationType == PlayerAnimationType.ARM_SWING) {
            PlayerAnimation.ARM_SWING.play(citizensNpc.entity as Player?)
        }
    }

}