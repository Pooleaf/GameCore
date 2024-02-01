package net.pooleaf.gamecore.reboot

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.channel.ChannelModule
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.support.bukkit.util.BukkitBroadcaster
import net.pooleaf.core.modules.support.common.exception.throwMessage
import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.GameCorePermission
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.scheduler.BukkitTask
import java.time.LocalDateTime

class RebootManager {

    var rebooting = false
    var rebootScheduled = false
    var rebootScheduledSender: CommandSender? = null
    var rebootScheduleTask: BukkitTask? = null


    /**
     * 서버를 재부팅합니다.
     * 접속 중인 플레이어를 로비로 이동시킨 후 재부팅됩니다.
     */
    fun reboot(sender: CommandSender? = null) {
        if (rebooting) {
            throwMessage("§c이미 재부팅 중입니다.")
            return
        }

        rebooting = true

        BukkitAsyncScope.launch {
            // 게임 중이면 게임 중단
            if (GameCore.game.isRunning) {
                BukkitBroadcaster.broadcastWarning("서버 재부팅으로 인해 게임이 중단되었습니다.")
                GameCore.unsafe.gameManager.cancelGame(sender, "서버 재부팅으로 인해 게임이 중단되었습니다.")
            }

            // 빠른 접속 비허용
            ChannelModule.getCurrentChannel().isAllowFastJoin = false
            ChannelModule.getCurrentChannel().save()

            // 공지
            BukkitBroadcaster.broadcast("§c서버 재부팅을 위해 로비로 이동됩니다.")
            BukkitBroadcaster.broadcastTitle("§c알림", "§c서버 재부팅을 위해 로비로 이동됩니다.")
            BukkitBroadcaster.broadcastSound(XSound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.4F, 1.0F)

            // 로비로 이동
            delay(3000L)
            Bukkit.getOnlinePlayers().forEach { ChannelModule.getLobbyChannelGroup().fastJoin(it.uniqueId) }

            // 재부팅
            delay(7000L)
            Bukkit.shutdown()
        }
    }

    /**
     * 서버 재부팅을 예약합니다.
     * 게임 중이 아닐 경우 즉시 재부팅하고,
     * 게임 중일 경우 게임이 종료되면 재부팅합니다.
     */
    fun scheduleReboot(sender: CommandSender? = null) {
        if (rebooting) {
            throwMessage("§c이미 재부팅 중입니다.")
            return
        }

        if (rebootScheduled) {
            throwMessage("§c이미 서버 재부팅이 예약되었습니다.")
            return
        }

        rebootScheduled = true
        rebootScheduledSender = sender

        // 관리자가 아니면 메시지 출력
        if (sender?.hasPermission(GameCorePermission.ADMIN) != true) {
            sender?.sendMessage("§a서버 재부팅이 예약되었습니다.")
        }

        // 관리자에게 메시지 출력
        Bukkit.getOnlinePlayers()
            .filter { it.hasPermission(GameCorePermission.ADMIN) }
            .forEach { it.sendMessage("§a서버 재부팅이 예약되었습니다.") }

        // 로그
        Logger.log("§a서버 재부팅이 예약되었습니다. (처리자: ${sender?.name ?: "알 수 없음"})")

        // 게임 중이 아니면 즉시 재부팅
        if (!GameCore.game.isCountingStarted) {
            reboot(sender)
        }
    }

    /**
     * 서버 재부팅 예약을 취소합니다.
     */
    fun cancelScheduleReboot(sender: CommandSender? = null) {
        if (rebooting) {
            throwMessage("§c이미 재부팅 중입니다.")
            return
        }

        if (!rebootScheduled) {
            throwMessage("§c아직 서버 재부팅이 예약되지 않았습니다.")
            return
        }

        rebootScheduled = false
        rebootScheduledSender = null

        // 관리자가 아니면 메시지 출력
        if (sender?.hasPermission(GameCorePermission.ADMIN) != true) {
            sender?.sendMessage("§c서버 재부팅 예약이 취소되었습니다.")
        }

        // 관리자에게 메시지 출력
        Bukkit.getOnlinePlayers()
            .filter { it.hasPermission(GameCorePermission.ADMIN) }
            .forEach { it.sendMessage("§c서버 재부팅 예약이 취소되었습니다.") }

        // 로그
        Logger.log("§c서버 재부팅 예약이 취소되었습니다. (처리자: ${sender?.name ?: "알 수 없음"})")
    }

    /**
     * 자동 재부팅 타이머를 시작합니다.
     */
    fun startAutoRebootTask() {
        // 0초 계산하여 스케줄러 시작
        val remainSecondUntilZero = 60L - LocalDateTime.now().second
        rebootScheduleTask = Bukkit.getScheduler().runTaskTimerAsynchronously(GameCore.gamePlugin, RebootScheduleTask(), remainSecondUntilZero * 20L, 60 * 20L)
    }

    /**
     * 자동 재부팅 타이머를 중지합니다.
     */
    fun stopAutoRebootTask() {
        rebootScheduleTask?.cancel()
    }

}