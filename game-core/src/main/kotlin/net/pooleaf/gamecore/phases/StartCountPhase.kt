package net.pooleaf.gamecore.phases

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.support.bukkit.util.BukkitBroadcaster
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.phase.Phase

open class StartCountPhase(
    val teleportToMap: Boolean = true
): Phase() {

    override suspend fun onStart() {
        GameCore.game.isCountingStarted = true

        BukkitBroadcaster.broadcastActionBar("§e잠시 후 게임이 시작됩니다.")
        BukkitBroadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3F, 0.7F)
    }

    override suspend fun onRun() {
        delay(5000L)

        // 카운트
        for (count in 5 downTo 1) {
            when (count) {
                in 4..5 -> {
                    BukkitBroadcaster.broadcastTitle("§e${count}")
                    BukkitBroadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3F, 0.7F)
                }
                in 1..3 -> {
                    BukkitBroadcaster.broadcastTitle("§c${count}")
                    BukkitBroadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3F, 0.7F)
                }
            }

            delay(1000L)
        }
    }

    override fun onEnd() {
        GameCore.unsafe.gameManager.onGameStarted()

        // 맵으로 텔레포트
        if (teleportToMap) {
            BukkitSyncScope.launch {
                GameCore.unsafe.gameManager.teleportToMap()
            }
        }
    }

}