package net.pooleaf.gamecore.phases

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.support.bukkit.particle.Particle
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.gamecore.Broadcaster
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.phase.Phase
import net.pooleaf.gamecore.utils.StringUtil
import org.bukkit.Location

abstract class WorldBorderUpdatePhase(): Phase() {

    // 경계선 변화 남은 시간
    var updateRemainingSeconds: Int? = null

    // 경계선 변화 지속 시간
    var updateDurationSeconds: Int? = null

    // 경계 파티클 Job
    var particleJob: Job? = null

    /**
     * 새로운 경계선 크기
     */
    abstract fun getNewWorldBorderSize(): Int

    /**
     * 경계선 크기가 변하기 전 대기 시간(초) (예: n초 뒤 경계선이 줄어듭니다.)
     */
    abstract fun getUpdateWaitSeconds(): Int

    /**
     * 초당 경계선이 변화할 크기
     */
    abstract fun getUpdateSizePerSeconds(): Int


    override fun onInit() {
        updateRemainingSeconds = null
        updateDurationSeconds = null

        particleJob?.cancel()
        particleJob = null
    }

    override suspend fun onStart() {
        GameCore.currentMap?.let { currentMap ->
            val currentWorldBorderSize = currentMap.currentWorldBorderSize

            // 크기가 변화하지 않을 경우 종료
            if (getNewWorldBorderSize() == currentWorldBorderSize) {
                end()
                return
            }

            val updateMessage = if (getNewWorldBorderSize() > currentWorldBorderSize) {
                "늘어납니다"
            } else {
                "줄어듭니다"
            }

            val updateTime = StringUtil.buildTimeStringWithColor(getUpdateWaitSeconds() * 1000L, CommonChatColor.WHITE, CommonChatColor.YELLOW)

            Broadcaster.broadcast("")
            Broadcaster.broadcast("${updateTime} §e후 맵의 경계가 ${updateMessage}.")
            Broadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3F, 0.7F)

            startParticleTimer()
        } ?: error("currentMap cannot be null")
    }

    override suspend fun onRun() {
        GameCore.currentMap?.let { currentMap ->
            val currentWorldBorderSize = currentMap.currentWorldBorderSize
            val updateMessage = if (getNewWorldBorderSize() > currentWorldBorderSize) {
                "늘어납니다"
            } else {
                "줄어듭니다"
            }

            // 경계선 변화 알림 메시지
            for (count in getUpdateWaitSeconds() downTo 1) {
                updateRemainingSeconds = count

                if (count <= 5) {
                    val updateTime = StringUtil.buildTimeStringWithColor(count * 1000L, CommonChatColor.WHITE, CommonChatColor.YELLOW)

                    Broadcaster.broadcast("${updateTime} §e후 맵의 경계가 ${updateMessage}.")
                    Broadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3F, 0.7F)
                }

                GameCore.unsafe.sideBarManager.sideBar?.let { it.update() }

                delay(1000L)
            }
            updateRemainingSeconds = null

            // 경계선 변화 시작
            updateDurationSeconds = currentMap.updateWorldBorder(getNewWorldBorderSize(), getUpdateSizePerSeconds())
            val updateDurationTime = StringUtil.buildTimeStringWithColor(updateDurationSeconds!! * 1000L, CommonChatColor.WHITE, CommonChatColor.YELLOW)

            Broadcaster.broadcast("§e맵의 경계가 ${updateMessage}.")
            Broadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3F, 0.7F)

            delay(updateDurationSeconds!! * 1000L)
            updateDurationSeconds = null
        } ?: error("currentMap cannot be null")
    }

    override fun onEnd() {
        onInit()
    }

    override fun onCancel() {
        onInit()
    }

    fun startParticleTimer() {
        particleJob = BukkitAsyncScope.launch {
            GameCore.currentMap?.let { map ->
                val centerLocation = map.centerLocation!!
                val newWorldBorderSize = getNewWorldBorderSize()

                val startX = (centerLocation.x - (newWorldBorderSize / 2)).toInt()
                val endX = (centerLocation.x + (newWorldBorderSize / 2)).toInt()
                val startZ = (centerLocation.z - (newWorldBorderSize / 2)).toInt()
                val endZ = (centerLocation.z + (newWorldBorderSize / 2)).toInt()

                while (!isEnded) {
                    GameCore.unsafe.playerManager.getOnlinePlayingPlayers().forEach { gamePlayer ->
                        val player = gamePlayer.player
                        val playerLocation = player.location

                        for (x in startX .. endX) {
                            val startZLocation = Location(playerLocation.world, x.toDouble(), playerLocation.y, startZ.toDouble())
                            val endZLocation = Location(playerLocation.world, x.toDouble(), playerLocation.y, endZ.toDouble())

                            Particle.SPELL_INSTANT.spawn(player, startZLocation, 0.0F, 1)
                            Particle.SPELL_INSTANT.spawn(player, endZLocation, 0.0F, 1)
                        }

                        for (z in startZ .. endZ) {
                            val startXLocation = Location(playerLocation.world, startX.toDouble(), playerLocation.y, z.toDouble())
                            val endXLocation = Location(playerLocation.world, endX.toDouble(), playerLocation.y, z.toDouble())

                            Particle.SPELL_INSTANT.spawn(player, startXLocation, 0.0F, 1)
                            Particle.SPELL_INSTANT.spawn(player, endXLocation, 0.0F, 1)
                        }
                    }

                    delay(300L)
                }
            }
        }
    }

    fun stopParticleTimer() {
        particleJob?.cancel()
        particleJob = null
    }

}