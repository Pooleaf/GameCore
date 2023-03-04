package net.pooleaf.gamecore.phase

import kotlinx.coroutines.*
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import java.time.LocalDateTime

open class Phase() {

    // 시작 여부
    var isStarted: Boolean = false

    // 종료 여부
    var isEnded: Boolean = false

    // 시작 시간
    var startedAt: LocalDateTime? = null

    // Phase Job
    var job: Job? = null


    /**
     * Phase가 초기화될 때 실행됩니다.
     */
    protected open fun onInit() {}

    /**
     * Phase가 시작될 때 실행됩니다.
     * Phase 시작 메시지 등을 담당합니다.
     */
    protected open suspend fun onStart() {}

    /**
     * Phase 시작 직후 실행됩니다.
     * Phase 중 일어날 일들을 담당합니다.
     */
    protected open suspend fun onRun() {}

    /**
     * Phase가 종료될 때 실행됩니다.
     * Phase 종료 메시지 등을 담당합니다.
     */
    protected open fun onEnd() {}

    /**
     * Phase가 중단될 때 실행됩니다.
     */
    protected open fun onCancel() {}

    /**
     * Phase를 초기화 시킵니다.
     */
    fun init() {
        isStarted = false
        isEnded = false

        startedAt = null

        onInit()
    }

    /**
     * Phase를 시작시키고 종료될 때까지 기다립니다.
     * 종료 시 자동으로 Phase가 종료됩니다.
     */
    suspend fun start() {
        if (isStarted) error("Phase already started.")

        isStarted = true
        startedAt = LocalDateTime.now()

        job = BukkitAsyncScope.launch {
            onStart()
            onRun()
        }
        job?.join()

        end()
    }

    /**
     * Phase를 종료합니다.
     */
    fun end() {
        if (!isStarted) error("Phase not started.")
        if (isEnded) error("Phase already ended.")

        job?.cancel() ?: error("job cannot be null")

        isEnded = true
        onEnd()
    }

    /**
     * Phase를 취소합니다.
     */
    fun cancel() {
        if (!isStarted) error("Phase not started")
        if (isEnded) error("Phase already ended.")

        job?.cancel() ?: error("job cannot be null")

        isEnded = true
        onCancel()
    }

}