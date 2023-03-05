package net.pooleaf.gamereplay

import net.pooleaf.core.modules.commonevent.CommonEventModule
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.core.plugin.BukkitCorePlugin
import org.bukkit.Bukkit

class GameReplayPlugin : BukkitCorePlugin() {

    companion object {
        lateinit var instance: GameReplayPlugin
    }


    override fun onStart() {
        instance = this

        prefix = "§c[ GameReplay ]"
        color = CommonChatColor.RED
        registerLoggerPrefix()

        if (Bukkit.getPluginManager().getPlugin("GameCore") == null) {
            init()
        } else {
            CommonEventModule.registerListener(this, GameReplayGameCoreBootstrap())
        }
    }

    internal fun init() {
        GameReplayApi.init()

        GameReplayApi.unsafe.sqlManager.connect()
        loadConfig()

        registerEventListeners()
        registerCommands()

        Logger.log("플러그인이 초기화되었습니다.")
    }

    override fun onEnd() {
        GameReplayApi.unsafe.sqlManager.close()
    }

    override fun onConfigLoaded() {
        GameReplayApi.loadConfig()
    }


}