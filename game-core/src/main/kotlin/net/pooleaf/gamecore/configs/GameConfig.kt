package net.pooleaf.gamecore.configs

import net.pooleaf.core.modules.annoconfig.common.SimpleAnnoConfig
import net.pooleaf.core.modules.annoconfig.common.anno.ConfigName
import java.io.File

class GameConfig(file: File?) : SimpleAnnoConfig(file) {

    @ConfigName("게임 시작 플레이어 수")
    var startPlayerCount: Int = 2

    @ConfigName("재접속 허용 시간(초)")
    var reconnectAllowSeconds: Int = 60 * 3

    @ConfigName("우승 허용 시간(초)")
    var winAllowSeconds: Int = 30

    @ConfigName("킬 유효 시간(초)")
    var killValidSeconds: Int = 10

    @ConfigName("어시스트 유효 시간(초)")
    var assistValidSeconds: Int = 10

    @ConfigName("게임 최대 시간(초)")
    var gameMaxSeconds: Int = 60 * 60

}