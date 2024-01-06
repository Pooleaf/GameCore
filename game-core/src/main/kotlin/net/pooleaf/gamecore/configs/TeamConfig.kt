package net.pooleaf.gamecore.configs

import net.pooleaf.core.modules.annoconfig.common.SimpleAnnoConfig
import net.pooleaf.core.modules.annoconfig.common.anno.ConfigName
import java.io.File

class TeamConfig(file: File?) : SimpleAnnoConfig(file) {

    @ConfigName("최대 팀 수")
    val maxTeamCount: Int = 999

    @ConfigName("팀당 플레이어 수")
    val playerCountPerTeam: Int = 1

    @ConfigName("PVP.허용")
    val allowPvp: Boolean = true

    @ConfigName("낮은 데미지 PVP.허용")
    val allowLowDamagePvp: Boolean = false

    @ConfigName("낮은 데미지 PVP.데미지 기준(이하)")
    val allowedLowDamage: Int = 1

    @ConfigName("데미지 0으로 변환.사용")
    val useDamageConvertToZero: Boolean = true

}