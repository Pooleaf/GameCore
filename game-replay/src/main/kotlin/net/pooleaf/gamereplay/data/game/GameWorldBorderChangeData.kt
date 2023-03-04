package net.pooleaf.gamereplay.data.game

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.coroutine.bukkit.BukkitNewAsyncScope
import net.pooleaf.core.modules.support.bukkit.particle.Particle
import net.pooleaf.gamecore.events.game.GameWorldBorderChangeEvent
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import net.pooleaf.gamereplay.replay.ReplayPlayer
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

/**
 * 팀 탈락 데이터
 */
data class GameWorldBorderChangeData(
    var tick: Long = 0,
    var centerX: Int = 0,
    var centerZ: Int = 0,
    var beforeSize: Int = 0,
    var newSize: Int = 0,
    var updateDurationSeconds: Int = 0
) : RecordData {

    override val type: String = "gameWorldBorderChange"

}

class GameWorldBorderChangeDataRecordListener : Listener {

    @EventHandler
    fun onGameWorldBorderChange(event: GameWorldBorderChangeEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val recordData = GameWorldBorderChangeData().apply {
            tick = GameReplayApi.unsafe.recordManager.record!!.currentTick.toLong()
            centerX = event.centerLocation.x.toInt()
            centerZ = event.centerLocation.z.toInt()
            beforeSize = event.beforeSize
            newSize = event.newSize
            updateDurationSeconds = event.updateDurationSeconds
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class GameWorldBorderChangeDataReplayHandler : RecordDataReplayHandler<GameWorldBorderChangeData>, Listener {

    var worldBorderDisplayJob: Job? = null


    override fun onPlay(recordData: GameWorldBorderChangeData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)

        if (isWorldBorderDisplayJobRunning()) {
            stopWorldBorderDisplayTimer()
        }

        startWorldBorderDisplayTimer(
            replayPlayer,
            viewer,
            recordData.tick,
            recordData.centerX,
            recordData.centerZ,
            recordData.beforeSize,
            recordData.newSize,
            recordData.updateDurationSeconds
        )
    }

    fun isWorldBorderDisplayJobRunning(): Boolean {
        return worldBorderDisplayJob?.let { it.isActive } == true
    }

    fun startWorldBorderDisplayTimer(
        replayPlayer: ReplayPlayer,
        viewer: Player,
        tick: Long,
        centerX: Int,
        centerZ: Int,
        beforeSize: Int,
        newSize: Int,
        updateDurationSeconds: Int
    ) {
        if (isWorldBorderDisplayJobRunning()) error("worldBorderDisplayTimer already running")

        worldBorderDisplayJob = BukkitNewAsyncScope.launch {
            while (!replayPlayer.isExit) {
                val runningSeconds = (replayPlayer.currentTick - tick) / 20

                val size: Float = if (runningSeconds >= updateDurationSeconds) {
                    newSize.toFloat()
                } else {
                    beforeSize + ((newSize - beforeSize).toFloat() / updateDurationSeconds * runningSeconds)
                }

                val startX = centerX - (size / 2)
                val endX = centerX + (size / 2)
                val startZ = centerZ - (size / 2)
                val endZ = centerZ + (size / 2)

                var x = startX
                while (x < endX) {
                    val startZLocation = Location(viewer.world, x.toDouble(), viewer.location.y, startZ.toDouble())
                    val endZLocation = Location(viewer.world, x.toDouble(), viewer.location.y, endZ.toDouble())

                    Particle.SPELL_INSTANT.spawn(viewer, startZLocation, 0.0F, 1)
                    Particle.SPELL_INSTANT.spawn(viewer, endZLocation, 0.0F, 1)
                    x++
                }

                var z = startZ
                while (z < endZ) {
                    val startXLocation = Location(viewer.world, startX.toDouble(), viewer.location.y, z.toDouble())
                    val endXLocation = Location(viewer.world, endX.toDouble(), viewer.location.y, z.toDouble())

                    Particle.SPELL_INSTANT.spawn(viewer, startXLocation, 0.0F, 1)
                    Particle.SPELL_INSTANT.spawn(viewer, endXLocation, 0.0F, 1)

                    z++
                }

                delay(300L)
            }
        }
    }

    fun stopWorldBorderDisplayTimer() {
        if (!isWorldBorderDisplayJobRunning()) error("worldBorderDisplayTimer already not running")

        worldBorderDisplayJob?.cancel()
        worldBorderDisplayJob = null
    }

}