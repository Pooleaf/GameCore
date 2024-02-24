package net.pooleaf.gamecore

import net.pooleaf.core.modules.commonevent.CommonEventModule
import net.pooleaf.gamecore.anticheat.AntiCheatBypassService
import net.pooleaf.gamecore.configs.*
import net.pooleaf.gamecore.events.GameCoreInitializedEvent
import net.pooleaf.gamecore.game.Game
import net.pooleaf.gamecore.game.GameManager
import net.pooleaf.gamecore.kit.KitManager
import net.pooleaf.gamecore.kit.KitService
import net.pooleaf.gamecore.map.DefaultGameMapManager
import net.pooleaf.gamecore.map.GameMap
import net.pooleaf.gamecore.map.GameMapManager
import net.pooleaf.gamecore.map.GameMapService
import net.pooleaf.gamecore.player.DefaultGamePlayerManager
import net.pooleaf.gamecore.player.GamePlayer
import net.pooleaf.gamecore.player.GamePlayerManager
import net.pooleaf.gamecore.player.GamePlayerService
import net.pooleaf.gamecore.quickbar.QuickBarManager
import net.pooleaf.gamecore.reboot.RebootManager
import net.pooleaf.gamecore.sidebar.GameSideBarManager
import net.pooleaf.gamecore.startitem.StartItemManager
import net.pooleaf.gamecore.startitem.StartItemService
import net.pooleaf.gamecore.supply.SupplyManager
import net.pooleaf.gamecore.supply.SupplyService
import net.pooleaf.gamecore.team.TeamManager
import net.pooleaf.gamecore.team.TeamNameTagManager
import net.pooleaf.gamecore.team.TeamService
import net.pooleaf.gamecore.vote.godmodeskip.GodModeSkipVoteManager
import net.pooleaf.gamecore.vote.map.MapVoteManager
import net.pooleaf.gamecore.vote.noenchantmode.NoEnchantModeVoteManager
import net.pooleaf.gamecore.vote.start.StartVoteManager
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

object GameCore {

    object unsafe {
        lateinit var gamePlugin: JavaPlugin

        lateinit var gameManager: GameManager

        lateinit var mapManager: GameMapManager<GameMap>
        lateinit var mapService: GameMapService

        lateinit var playerManager: GamePlayerManager<GamePlayer>
        lateinit var playerService: GamePlayerService

        lateinit var teamManager: TeamManager
        lateinit var teamNameTagManager: TeamNameTagManager
        lateinit var teamService: TeamService

        lateinit var startVoteManager: StartVoteManager
        lateinit var mapVoteManager: MapVoteManager
        lateinit var godModeSkipVoteManager: GodModeSkipVoteManager
        lateinit var noEnchantModeVoteManager: NoEnchantModeVoteManager

        lateinit var quickBarManager: QuickBarManager
        lateinit var sideBarManager: GameSideBarManager

        lateinit var kitManager: KitManager
        lateinit var kitService: KitService

        lateinit var startItemManager: StartItemManager
        lateinit var startItemService: StartItemService

        lateinit var supplyManager: SupplyManager
        lateinit var supplyService: SupplyService

        lateinit var rebootManager: RebootManager

        lateinit var antiCheatBypassService: AntiCheatBypassService


        val gameConfig: GameConfig by lazy {
            GameConfig(File(GameCore.gamePlugin.dataFolder, "game-config.yml"))
        }

        val spawnConfig: SpawnConfig by lazy {
            SpawnConfig(File(GameCore.gamePlugin.dataFolder, "spawn-config.yml"))
        }

        val quickBarConfig: QuickBarConfig by lazy {
            QuickBarConfig(File(GameCore.gamePlugin.dataFolder, "quickbar-config.yml"))
        }

        val teamConfig: TeamConfig by lazy {
            TeamConfig(File(GameCore.gamePlugin.dataFolder, "team-config.yml"))
        }

        val autoRebootConfig: AutoRebootConfig by lazy {
            AutoRebootConfig(File(GameCore.gamePlugin.dataFolder, "auto-reboot-config.yml"))
        }


        fun init() {
            gameManager = GameManager()

            mapManager = DefaultGameMapManager()
            mapService = GameMapService()

            playerManager = DefaultGamePlayerManager()
            playerService = GamePlayerService()

            teamManager = TeamManager()
            teamNameTagManager = TeamNameTagManager()
            teamService = TeamService()

            startVoteManager = StartVoteManager()
            mapVoteManager = MapVoteManager()
            godModeSkipVoteManager = GodModeSkipVoteManager()
            noEnchantModeVoteManager = NoEnchantModeVoteManager()

            quickBarManager = QuickBarManager()
            sideBarManager = GameSideBarManager()

            kitManager = KitManager()
            kitService = KitService()

            startItemManager = StartItemManager()
            startItemService = StartItemService()

            supplyManager = SupplyManager()
            supplyService = SupplyService()

            rebootManager = RebootManager()

            antiCheatBypassService = AntiCheatBypassService()

            loadConfig()
        }

        fun loadConfig() {
            gameConfig.load()
            gameConfig.save()

            spawnConfig.load()
            spawnConfig.save()

            quickBarConfig.load()
            quickBarConfig.save()

            teamConfig.load()
            teamConfig.save()

            autoRebootConfig.load()
            autoRebootConfig.save()

            mapService.loadMapConfigs()

            kitService.loadKitConfigs()
            startItemService.loadAllStartItemConfig()
            supplyService.loadSupplyConfigs()
        }
    }

    val gamePlugin
        get() = unsafe.gamePlugin

    val gameConfig
        get() = unsafe.gameConfig

    val spawnConfig
        get() = unsafe.spawnConfig

    val quickBarConfig
        get() = unsafe.quickBarConfig

    val teamConfig
        get() = unsafe.teamConfig

    val autoRebootConfig
        get() = unsafe.autoRebootConfig


    val game
        get() = unsafe.gameManager.game

    val currentMap
        get() = unsafe.mapManager.currentMap


    fun init(
        gamePlugin: JavaPlugin,
        game: Game
    ) {
        unsafe.gamePlugin = gamePlugin

        unsafe.init()
        unsafe.gameManager.game = game

        game.init()
        game.isInitialized = true

        // 이벤트
        CommonEventModule.callEvent(GameCoreInitializedEvent())
    }

    fun loadConfig() {
        unsafe.loadConfig()
    }

}