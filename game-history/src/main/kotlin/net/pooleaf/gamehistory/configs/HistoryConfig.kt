package net.pooleaf.gamehistory.configs

import net.pooleaf.core.modules.annoconfig.common.SimpleAnnoConfig
import net.pooleaf.core.modules.annoconfig.common.anno.ConfigName
import org.bukkit.Bukkit
import java.io.File

class HistoryConfig(file: File?) : SimpleAnnoConfig(file) {

    @ConfigName("기록 활성화")
    var isEnableHistory: Boolean = (Bukkit.getPluginManager().getPlugin("GameCore") != null)

}