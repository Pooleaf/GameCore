package net.pooleaf.gamecore.supply

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.annoconfig.AnnoConfigModule
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.support.bukkit.util.BukkitBroadcaster
import net.pooleaf.core.modules.support.common.component.SimpleComponentBuilder
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.utils.FireworkUtil
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import java.io.File

class SupplyService {

    val supplyFolder = File(GameCore.gamePlugin.dataFolder, "supply")


    /**
     * 킷을 저장합니다.
     */
    fun saveSupplyConfig(supply: Supply) {
        supplyFolder.mkdirs()

        val file = File(supplyFolder, "${supply.name}.yml")
        AnnoConfigModule.save(file, supply)
    }

    /**
     * 모든 킷을 저장합니다.
     */
    fun saveSupplyConfigs() {
        GameCore.unsafe.supplyManager.values().forEach { saveSupplyConfig(it) }
    }

    /**
     * 킷을 불러옵니다.
     */
    fun loadSupplyConfig(name: String) {
        val supply = GameCore.unsafe.supplyManager.get(name) ?: Supply()
        supply.name = name
        GameCore.unsafe.supplyManager.set(name, supply)

        val file = File(supplyFolder, "${supply.name}.yml")
        AnnoConfigModule.load(file, supply)
    }

    /**
     * 모든 킷을 불러옵니다.
     */
    fun loadSupplyConfigs() {
        supplyFolder.mkdirs()

        supplyFolder.listFiles().filter { it.name.endsWith(".yml") }
            .forEach { loadSupplyConfig(it.name.substringBefore(".yml")) }
    }

    /**
     * 킷을 삭제합니다.
     */
    fun deleteSupplyConfig(name: String) {
        val file = File(supplyFolder, "${name}.yml")
        if (file.exists()) {
            file.delete()
        }
    }

    /**
     * 모든 킷을 삭제합니다.
     */
    fun deleteSupplyConfigs() {
        GameCore.unsafe.supplyManager.values().forEach { deleteSupplyConfig(it.name) }
    }

    /**
     * 보급품을 해당 위치에 생성합니다.
     * 만약 해당 위치에 보급품이 존재할 경우 한칸 위에 생성됩니다.
     * Primary Thread에서만 사용할 수 있습니다.
     */
    fun createSupply(supply: Supply, location: Location) {
        if (!Bukkit.isPrimaryThread()) error("createSupply() can only be used in primary thread")
        if (!GameCore.game.isGameStarted) error("Game is not started")

        var supplyLocation = location.block.location

        // 해당 위치에 보급품이 존재할 경우 한칸 위에 생성
        if (GameCore.unsafe.supplyManager.getCreatedSupply(supplyLocation) != null){
            supplyLocation.add(0.0 , 1.0, 0.0)
        }

        BukkitSyncScope.launch {
            // 생성
            supplyLocation.block.setType(Material.CHEST)

            // 폭죽
            val fireworkLocation = supplyLocation.clone().add(0.5, 0.0, 0.5)
            for (i in 1..3) {
                if (!GameCore.game.isGameStarted) return@launch

                delay(300L)
                FireworkUtil.shootRandomFirework(fireworkLocation)
            }
        }

        // 보급품 정보
        val supplyBlock = SupplyBlock(supply, supplyLocation)
        GameCore.unsafe.supplyManager.createdSupply.add(supplyBlock)

        // 메시지
        BukkitBroadcaster.broadcast("§b[X: §f${supplyLocation.x.toInt()}§b, Y: §f${supplyLocation.y.toInt()}§b, Z: §f${supplyLocation.z.toInt()}§b] 위치에 보급품이 생성되었습니다.")
        BukkitBroadcaster.broadcast(
            SimpleComponentBuilder("§b보급품 위치는 §f'/보급품 기록' §b명령어로 다시 확인할 수 있습니다.")
                .hoverShowText("클릭 시 보급품 기록을 확인합니다.")
                .clickRunCommand("/보급품 기록")
                .build()
        )
        BukkitBroadcaster.broadcastSound(XSound.ENTITY_ITEM_PICKUP, 0.4F, 1.0F)
    }

    /**
     * 현재 맵의 랜덤 위치에 보급품을 생성합니다.
     * 맵 경계 안에만 생성됩니다.
     */
    fun createSupplyRandomLocation(supply: Supply) {
        if (!Bukkit.isPrimaryThread()) error("createSupplyRandomLocation() can only be used in primary thread")

        val currentMap = GameCore.currentMap ?: error("currentMap cannot be null")
        var location = currentMap.getRandomLocation(true)

        location?.let { createSupply(supply, it) }
    }

    /**
     * 현재 맵의 랜덤 위치에 랜덤 보급품을 생성합니다.
     * 맵 경계 안에만 생성됩니다.
     */
    fun createRandomSupplyRandomLocation() {
        if (!Bukkit.isPrimaryThread()) error("createRandomSupplyRandomLocation() can only be used in primary thread")

        val supply = GameCore.unsafe.supplyManager.getRandomSupply()
        supply?.let { createSupplyRandomLocation(it) }
    }

}
