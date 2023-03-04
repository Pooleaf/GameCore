package net.pooleaf.gamecore.supply

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.server.v1_8_R3.EnumParticle
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.support.bukkit.particle.Particle
import net.pooleaf.core.modules.support.common.manager.AbstractSyncManager
import net.pooleaf.gamecore.GameCore
import org.bukkit.Location
import java.util.*

class SupplyManager : AbstractSyncManager<String, Supply>() {

    // 맵에 생성된 보급품
    val createdSupply = arrayListOf<SupplyBlock>()

    var supplyCreateJob: Job? = null

    var supplyParticleJob: Job? = null


    /**
     * [Supply.probabilityRatio]를 반영하여 랜덤 보급품을 반환합니다.
     * 보급품이 없을 경우 null을 반환합니다.
     */
    fun getRandomSupply(): Supply? {
        val ratioSum = GameCore.unsafe.supplyManager.values().sumOf { it.probabilityRatio }
        val randomRatio = Random().nextInt(ratioSum)
        var currentRatio = 0
        GameCore.unsafe.supplyManager.values().forEach { supply ->
            if (currentRatio <= randomRatio && randomRatio < currentRatio + supply.probabilityRatio) {
                return supply
            }

            currentRatio += supply.probabilityRatio
        }

        return null
    }

    /**
     * 해당 위치에 있는 생성된 보급품을 반환합니다.
     * 없을 경우 null을 반환합니다.
     */
    fun getCreatedSupply(location: Location): SupplyBlock? {
        return createdSupply.firstOrNull { it.location == location }
    }

    /**
     * 보급품 생성 타이머 작동 여부를 반환합니다.
     */
    fun isSupplyCreateTimerRunning(): Boolean {
        return supplyCreateJob?.let { it.isActive } == true
    }

    /**
     * 보급품 생성 타이머를 시작합니다.
     */
    fun startSupplyCreateTimer(supplyCreateIntervalSeconds: Int) {
        if (isSupplyCreateTimerRunning()) error("supplyCreateJob is already started")

        supplyCreateJob = BukkitAsyncScope.launch {
            while (GameCore.game.isGameStarted) {
                delay(supplyCreateIntervalSeconds * 1000L)

                BukkitSyncScope.launch {
                    GameCore.unsafe.supplyService.createRandomSupplyRandomLocation()
                }
            }
        }
    }

    /**
     * 보급품 생성 타이머를 중단합니다.
     */
    fun stopSupplyCreateTimer() {
        if (!isSupplyCreateTimerRunning()) error("supplyCreateJob is not started")

        supplyCreateJob?.cancel()
        supplyCreateJob = null
    }

    /**
     * 보급품 파티클 타이머 작동 여부를 반환합니다.
     */
    fun isSupplyParticleTimerRunning(): Boolean {
        return supplyParticleJob?.let { it.isActive } == true
    }

    /**
     * 보급품 파티클 타이머를 시작합니다.
     */
    fun startSupplyParticleTimer() {
        if (isSupplyParticleTimerRunning()) error("supplyParticleJob is already started")

        supplyParticleJob = BukkitAsyncScope.launch {
            while (GameCore.game.isGameStarted) {
                delay(300L)

                GameCore.unsafe.supplyManager.createdSupply.filter { it.usedBy == null }
                    .forEach {
                        Particle.RED_DUST.spawn(it.location.clone().add(0.5, 1.5, 0.5), 0.0F, 10)
                    }
            }
        }
    }

    /**
     * 보급품 파티클 타이머를 중단합니다.
     */
    fun stopSupplyParticleTimer() {
        if (!isSupplyParticleTimerRunning()) error("supplyCreateJob is not started")

        supplyParticleJob?.cancel()
        supplyParticleJob = null
    }

}