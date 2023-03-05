package net.pooleaf.gamereplay.configs

import net.pooleaf.core.modules.annoconfig.common.SimpleAnnoConfig
import net.pooleaf.core.modules.annoconfig.common.anno.ConfigName
import org.bukkit.Bukkit
import java.io.File

class ReplayConfig(file: File?) : SimpleAnnoConfig(file) {

    @ConfigName("리플레이 기록 서버.활성화")
    var isRecordServer: Boolean = (Bukkit.getPluginManager().getPlugin("GameCore") != null)

    @ConfigName("리플레이 재생 서버.활성화")
    var isReplayPlayServer: Boolean = (Bukkit.getPluginManager().getPlugin("GameCore") == null)

}