package net.pooleaf.gamecore.configs

import net.pooleaf.core.modules.annoconfig.common.SimpleAnnoConfig
import net.pooleaf.core.modules.annoconfig.common.anno.ConfigName
import java.io.File

class QuickBarConfig(file: File?) : SimpleAnnoConfig(file) {

    @ConfigName("대기 퀵바.시작 투표 사용")
    var useWaitingStartVote: Boolean = true

    @ConfigName("대기 퀵바.맵 투표 사용")
    var useWaitingMapVote: Boolean = true

}