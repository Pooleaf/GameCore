package net.pooleaf.gamecore.phase

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import org.bukkit.Bukkit

open class PhasePipeline {

    val phases = mutableListOf<Phase>()

    // 현재 Phase
    var currentPhase: Phase? = null

    // Pipeline Job
    var job: Job? = null


    /**
     * PhasePipeline을 초기화합니다.
     */
    fun init() {
        phases.forEach { it.init() }
        currentPhase = null
        job = null
    }

    /**
     * Phase를 추가합니다.
     */
    fun addPhase(phase: Phase): PhasePipeline {
        phases.add(phase)
        return this
    }

    /**
     * 모든 Phase를 실행시킵니다.
     * 비동기 쓰레드에서 진행됩니다.
     */
    suspend fun runPhases() {
        job = BukkitAsyncScope.launch {
            phases.forEach {
                currentPhase = it
                it.start()
            }
        }
    }

    /**
     * Phase 실행을 중단합니다.
     */
    fun cancelPhases() {
        if (!isRunning()) error("PhasePipeline is not running")

        job?.cancel()
        currentPhase?.cancel()
    }

    /**
     * Phase 실행 중 여부를 반환합니다.
     */
    fun isRunning(): Boolean {
        return job?.isActive == true
    }

    /**
     * Phase 실행 완료 여부를 반환합니다.
     */
    fun isEnded(): Boolean {
        return job?.isCompleted == true
    }

}