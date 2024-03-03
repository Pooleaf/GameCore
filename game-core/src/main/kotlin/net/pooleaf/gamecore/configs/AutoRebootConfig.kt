package net.pooleaf.gamecore.configs

import net.pooleaf.core.modules.annoconfig.common.SimpleAnnoConfig
import net.pooleaf.core.modules.annoconfig.common.anno.ConfigName
import java.io.File

class AutoRebootConfig(file: File?) : SimpleAnnoConfig(file) {

    @ConfigName("게임 종료 후 자동 재부팅.사용")
    var useAutoRebootOnGameEnd: Boolean = true

    @ConfigName("게임 종료 후 자동 재부팅.같은 채널 그룹으로 이동")
    var useAutoRebootOnGameEndSendToCurrentGroup: Boolean = true

    @ConfigName("게임 종료 후 자동 재부팅.로비 채널 그룹으로 이동")
    var useAutoRebootOnGameEndSendToLobbyGroup: Boolean = false

    @ConfigName("자동 재부팅 예약.사용")
    var useAutoRebootReserve: Boolean = true

    @ConfigName("자동 재부팅 예약.시간")
    var autoRebootReserveTimes: ArrayList<String> = arrayListOf("'05:00'")

    @ConfigName("자동 재부팅 예약.채널별 시간 Offset 사용")
    var useAutoRebootReserveTimeOffsetByChannel: Boolean = true

}