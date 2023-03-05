package net.pooleaf.gamereplay.configs

import net.pooleaf.core.modules.annoconfig.common.SimpleAnnoConfig
import net.pooleaf.core.modules.annoconfig.common.anno.ConfigName
import org.bukkit.Location
import java.io.File

class SpawnConfig(file: File?) : SimpleAnnoConfig(file) {

    @ConfigName("스폰 위치")
    var spawnLocation: Location? = null

}