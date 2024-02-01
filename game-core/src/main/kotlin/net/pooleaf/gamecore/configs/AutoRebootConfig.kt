package net.pooleaf.gamecore.configs

import net.pooleaf.core.modules.annoconfig.common.SimpleAnnoConfig
import net.pooleaf.core.modules.annoconfig.common.anno.ConfigName
import java.io.File

class AutoRebootConfig(file: File?) : SimpleAnnoConfig(file) {

    @ConfigName("자동 재부팅.사용")
    var useAutoReboot: Boolean = true

    @ConfigName("자동 재부팅.시간")
    var autoRebootTimes: ArrayList<String> = arrayListOf("'05:00'")

    @ConfigName("자동 재부팅.채널별 시간 Offset 사용")
    var useAutoRebootTimeOffsetByChannel: Boolean = true

}