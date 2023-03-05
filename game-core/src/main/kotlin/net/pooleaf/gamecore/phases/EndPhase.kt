package net.pooleaf.gamecore.phases

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.support.bukkit.util.BukkitBroadcaster
import net.pooleaf.core.modules.support.common.util.toMillis
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.phase.Phase
import net.pooleaf.gamecore.utils.FireworkUtil
import org.bukkit.Location
import org.bukkit.entity.Player

open class EndPhase(): Phase() {

    override suspend fun onStart() {
        // 게임 우승 조건이 안될 경우 중단
        if (!GameCore.unsafe.gameManager.canEnd()
            // 우승 가능 시간이 안될 경우
            || System.currentTimeMillis() - GameCore.game.startedAt!!.toMillis() < GameCore.gameConfig.winAllowSeconds) {
            GameCore.unsafe.gameManager.stopGame()
        }


        // 우승
        val winnerTeam = GameCore.unsafe.gameManager.onGameEnd()

        winnerTeam?.let { winnerTeam ->
            val winnerPlayers = winnerTeam?.players
            val winnerPlayerNames = winnerPlayers?.joinToString { it.displayName } ?: "?"

            // 우승 타이틀
            BukkitBroadcaster.broadcastTitle("§e우승", "§f${winnerPlayerNames}", 10 * 20)

            // 사운드
            BukkitBroadcaster.broadcastSound(XSound.ENTITY_PLAYER_LEVELUP, 0.4F, 0.5F)

            // 우승자 주변에 폭죽 날리기
            winnerPlayers?.forEach { gamePlayer ->
                BukkitSyncScope.launch {
                    gamePlayer.player?.let { player ->
                        for (i in 1..5) {
                            if (player.isOnline) {
                                shootRandomFirework(player)
                            }
                            delay(200L)
                        }
                    }
                }
            }

            // 다시 시작 액션바
            for (count in 15 downTo 1) {
                when (count) {
                    in 4..10 -> {
                        BukkitBroadcaster.broadcastActionBar("§e${count}§c초 후 게임이 다시 시작됩니다.")
                    }
                    in 1..3 -> {
                        BukkitBroadcaster.broadcastActionBar("§e${count}§c초 후 게임이 다시 시작됩니다.")
                        BukkitBroadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3F, 0.7F)
                    }
                }

                delay(1000L)
            }
        }
    }

    override fun onEnd() {
        BukkitBroadcaster.removeActionBar()

        // 게임 리셋
        BukkitAsyncScope.launch {
            GameCore.unsafe.gameManager.resetGame()
        }
    }

    private fun shootRandomFirework(player: Player) {
        val location: Location = player.location
        location.add(Math.random() * 10 - 5, 0.0, Math.random() * 10 - 5)

        FireworkUtil.shootRandomFirework(location)
    }

}