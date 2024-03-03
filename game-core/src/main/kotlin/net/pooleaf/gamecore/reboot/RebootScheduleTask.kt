package net.pooleaf.gamecore.reboot

import net.pooleaf.core.modules.channel.ChannelModule
import net.pooleaf.core.modules.support.common.exception.MessageException
import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.gamecore.GameCore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RebootScheduleTask : Runnable {

    override fun run() {
        if (GameCore.autoRebootConfig.autoRebootReserveTimes.isEmpty()) return

        // 채널 명에서 숫자만 추출하여 분 offset으로 사용함
        var channelOffsetMinutes = 0L

        if (GameCore.autoRebootConfig.useAutoRebootReserveTimeOffsetByChannel) {
            val channelName = ChannelModule.getCurrentChannelName() ?: return

            val channelNumber = channelName.replace(Regex("[^0-9]"), "")
            if (channelNumber.isEmpty()) return

            channelOffsetMinutes = channelNumber.toLong()
        }

        // 현재 시간이 자동 재부팅 설정에 들어가 있는지 확인 후 재부팅 예약
        val now = LocalDateTime.now().minusMinutes(channelOffsetMinutes)
        val dateFormat = now.format(DateTimeFormatter.ofPattern("HH:mm"))

        if (GameCore.autoRebootConfig.autoRebootReserveTimes.contains(dateFormat)) {
            try {
                GameCore.unsafe.rebootManager.scheduleReboot()
            } catch (exception: MessageException) {
                Logger.log(exception.message)
            }
        }
    }

}