package net.pooleaf.gamecore.configs

import net.pooleaf.core.modules.annoconfig.common.SimpleAnnoConfig
import net.pooleaf.core.modules.annoconfig.common.anno.ConfigName
import java.io.File

class TeamConfig(file: File?) : SimpleAnnoConfig(file) {

    @ConfigName("최대 팀 수")
    val maxTeamCount: Int = 999

    @ConfigName("팀당 플레이어 수")
    val playerCountPerTeam: Int = 1

}