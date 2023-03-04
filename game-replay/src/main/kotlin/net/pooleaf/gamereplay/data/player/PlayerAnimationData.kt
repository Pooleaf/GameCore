package net.pooleaf.gamereplay.data.player

import net.citizensnpcs.util.PlayerAnimation
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAnimationEvent
import org.bukkit.event.player.PlayerAnimationType
import java.util.*

data class PlayerAnimationData(
    var playerUuid: UUID? = null,
    var animationType: String? = null
) : RecordData {

    override val type: String = "playerAnimation"

}

class PlayerAnimationDataRecordListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerAnimation(event: PlayerAnimationEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val player = event.player
        if (!GameReplayApi.unsafe.recordManager.isRecordingTargetPlayer(player)) return

        val recordData = PlayerAnimationData().apply {
            playerUuid = player.uniqueId
            animationType = event.animationType.name
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class PlayerAnimationDataReplayHandler : RecordDataReplayHandler<PlayerAnimationData> {

    override fun onPlay(recordData: PlayerAnimationData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val citizensNpc = replayPlayer.virtualPlayerManager.get(recordData.playerUuid)?.citizensNpc ?: return

        val animationType = PlayerAnimationType.valueOf(recordData.animationType!!)
        if (animationType == PlayerAnimationType.ARM_SWING) {
            PlayerAnimation.ARM_SWING.play(citizensNpc.entity as Player)
        }
    }

}