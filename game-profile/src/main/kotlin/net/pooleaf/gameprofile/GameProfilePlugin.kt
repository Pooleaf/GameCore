package net.pooleaf.gameprofile

import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.core.plugin.BukkitCorePlugin

class GameProfilePlugin : BukkitCorePlugin() {

    companion object {
        lateinit var instance: GameProfilePlugin
    }


    override fun onStart() {
        instance = this

        prefix = "§c[ GameProfile ]"
        color = CommonChatColor.RED
        registerLoggerPrefix()

        registerEventListeners()
        registerCommands()
    }

}