package net.pooleaf.gamereplay.replay

import kotlinx.coroutines.launch
import net.citizensnpcs.api.trait.trait.Owner
import net.citizensnpcs.trait.GameModeTrait
import net.citizensnpcs.trait.Gravity
import net.citizensnpcs.trait.SkinTrait
import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.support.bukkit.util.BukkitBroadcaster
import net.pooleaf.core.modules.support.bukkit.util.TeleportUtil
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.GameReplayPlugin
import net.pooleaf.gamereplay.data.datas.block.*
import net.pooleaf.gamereplay.data.datas.entity.*
import net.pooleaf.gamereplay.data.datas.game.GameWorldBorderChangeData
import net.pooleaf.gamereplay.data.datas.player.*
import net.pooleaf.gamereplay.events.ReplayExitEvent
import net.pooleaf.gamereplay.events.ReplayInitEvent
import net.pooleaf.gamereplay.events.ReplayJumpToEvent
import net.pooleaf.gamereplay.events.ReplayPlayStartEvent
import net.pooleaf.gamereplay.replay.virtual.VirtualLocation
import net.pooleaf.gamereplay.replay.virtual.block.VirtualBlock
import net.pooleaf.gamereplay.replay.virtual.block.VirtualBlockManager
import net.pooleaf.gamereplay.replay.virtual.entity.VirtualEntity
import net.pooleaf.gamereplay.replay.virtual.entity.VirtualEntityManager
import net.pooleaf.gamereplay.replay.virtual.player.VirtualPlayer
import net.pooleaf.gamereplay.replay.virtual.player.VirtualPlayerManager
import net.pooleaf.gamereplay.replay.virtual.worldborder.VirtualWorldBorder
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.*

/**
 * 리플레이 재생기
 */
class ReplayPlayer(
    val viewer: Player,
    val replay: Replay
) {

    companion object {
        // 실제 Entity와 ID가 겹치지 않도록 사용하는 Offset
        val ENTITY_ID_OFFSET = 10000000
    }


    var currentTick: Float = 0.0F
        private set

    private var lastPlayedTick: Long = -1

    var playSpeed: Float = 1.0F

    private var replayTask: BukkitTask? = null

    var isExit: Boolean = false


    // 커스텀 데이터
    val etcDatas = hashMapOf<String, Any>()

    // 가상 블럭 관리자
    val virtualBlockManager: VirtualBlockManager = VirtualBlockManager()

    // 가상 엔티티 관리자
    val virtualEntityManager: VirtualEntityManager = VirtualEntityManager()

    // 가상 플레이어 관리자
    val virtualPlayerManager: VirtualPlayerManager = VirtualPlayerManager()

    // 가상 경계선
    val virtualWorldBorder: VirtualWorldBorder = VirtualWorldBorder()


    fun isRunning(): Boolean {
        return replayTask != null
    }

    /**
     * 초기화 및 캐싱
     */
    fun init() {
        // 블럭 데이터 캐싱
        replay.recordDatas.forEach { tick, datas ->
            datas.filter {
                it is BlockChangeData
                        || it is BlockDamageData
                        || it is EntityChangeBlockData
                        || it is ExplodeData
                        || it is MultiBlockChangeData
            }.forEach { data ->
                val virtualLocations = when (data) {
                    is BlockChangeData -> listOf(VirtualLocation(data.x.toDouble(), data.y.toDouble(), data.z.toDouble()))
                    is BlockDamageData -> listOf(VirtualLocation(data.x.toDouble(), data.y.toDouble(), data.z.toDouble()))
                    is EntityChangeBlockData -> listOf(VirtualLocation(data.x.toDouble(), data.y.toDouble(), data.z.toDouble()))
                    is ExplodeData -> data.blockInfos.map { VirtualLocation(it.x.toDouble(), it.y.toDouble(), it.z.toDouble()) }
                    is MultiBlockChangeData -> data.blockChangeInfos.map { VirtualLocation(it.x.toDouble(), it.y.toDouble(), it.z.toDouble()) }
                    else -> return@forEach
                }

                virtualLocations.forEachIndexed { index, virtualLocation ->
                    // VirtualBlock 생성
                    var virtualBlock = virtualBlockManager.get(virtualLocation)
                    if (virtualBlock == null) {
                        virtualBlock = VirtualBlock(virtualLocation)
                        virtualBlockManager.set(virtualLocation, virtualBlock)
                    }

                    // VirtualBlock 기록 생성
                    var virtualBlockHistory = virtualBlock.histories.get(tick)

                    if (virtualBlockHistory == null) {
                        virtualBlockHistory = VirtualBlock(virtualLocation)
                        virtualBlock.histories.put(tick, virtualBlockHistory)
                    }

                    // 데이터별 기록 생성
                    when (data) {
                        is BlockChangeData -> {
                            virtualBlockHistory.typeId = data.blockTypeId
                            virtualBlockHistory.typeData = data.blockData
                        }
                        is BlockDamageData -> {
                            virtualBlockHistory.damageState = data.state
                        }
                        is EntityChangeBlockData -> {
                            virtualBlockHistory.typeId = data.blockTypeId
                            virtualBlockHistory.typeData = data.blockData
                        }
                        is ExplodeData -> {
                            virtualBlockHistory.typeId = 0
                            virtualBlockHistory.typeData = 0
                        }
                        is MultiBlockChangeData -> {
                            virtualBlockHistory.typeId = data.blockChangeInfos.get(index).blockTypeId
                            virtualBlockHistory.typeData = data.blockChangeInfos.get(index).blockData.toByte()
                        }
                        else -> return@forEach
                    }
                }
            }

        }

        // 엔티티 데이터 캐싱
        replay.recordDatas.forEach { tick, datas ->
            datas.filter {
                it is CollectData
                        || it is EntityDestroyData
                        || it is EntityTeleportData
                        || it is EntityVelocityData
                        || it is ItemMetaDataData
                        || it is SpawnEntityData
            }.forEach { data ->
                val entityIds: List<Int> = when (data) {
                    is CollectData -> listOf(data.collectedEntityId)
                    is EntityDestroyData -> data.entityIds.toList()
                    is EntityTeleportData -> listOf(data.entityId)
                    is EntityVelocityData -> listOf(data.entityId)
                    is ItemMetaDataData -> listOf(data.entityId)
                    is SpawnEntityData -> listOf(data.entityId)
                    else -> return@forEach
                }

                entityIds.forEach { entityId ->
                    var virtualEntity = virtualEntityManager.get(entityId)

                    if (virtualEntity == null) {
                        virtualEntity = VirtualEntity(entityId)
                        virtualEntityManager.set(entityId, virtualEntity)
                    }

                    val histories = virtualEntity.histories.getOrElse(tick) { arrayListOf() }
                    virtualEntity.histories.set(tick, histories)

                    histories.add(data)
                }
            }
        }

        // 가상 플레이어 NPC 생성
        replay.recordedPlayers.forEach { uuid ->
            val commonPlayer = CommonSenderModule.getPlayer(uuid)

            val npcName = commonPlayer?.name ?: "Unknown"
            val citizensNpc = virtualPlayerManager.npcRegistry.createNPC(EntityType.PLAYER, npcName)
            citizensNpc.isProtected = true
            citizensNpc.getOrAddTrait(Owner::class.java).setOwner(viewer)
            citizensNpc.getOrAddTrait(Gravity::class.java).toggle()
            citizensNpc.getOrAddTrait(GameModeTrait::class.java).gameMode = GameMode.CREATIVE
            citizensNpc.getOrAddTrait(SkinTrait::class.java).setSkinName(commonPlayer?.name, true)
            citizensNpc.spawn(viewer.location)

            // NPC 가리기
            Bukkit.getOnlinePlayers().filter { it.uniqueId != viewer.uniqueId }
                .forEach { it.hidePlayer(citizensNpc.entity as Player?) }

            val virtualPlayer = VirtualPlayer(uuid, citizensNpc)
            virtualPlayerManager.set(uuid, virtualPlayer)
        }

        // 가상 플레이어 캐싱
        replay.recordDatas.forEach { tick, datas ->
            datas.filter {
                it is PlayerAnimationData
                        || it is PlayerChatData
                        || it is PlayerDamageData
                        || it is PlayerEquipmentChangeData
                        || it is PlayerHealthChangeData
                        || it is PlayerMetaDataData
                        || it is PlayerMoveData
                        || it is PlayerTeleportData
                        || it is PlayerHideData
                        || it is PlayerShowData
            }.forEach { data ->
                val playerUuid: UUID = when (data) {
                    is PlayerAnimationData -> data.playerUuid!!
                    is PlayerChatData -> data.playerUuid!!
                    is PlayerDamageData -> data.playerUuid!!
                    is PlayerEquipmentChangeData -> data.playerUuid!!
                    is PlayerHealthChangeData -> data.playerUuid!!
                    is PlayerMetaDataData -> data.playerUuid!!
                    is PlayerMoveData -> data.playerUuid!!
                    is PlayerTeleportData -> data.playerUuid!!
                    is PlayerHideData -> data.playerUuid!!
                    is PlayerShowData -> data.playerUuid!!
                    else -> return@forEach
                }

                var virtualPlayer = virtualPlayerManager.get(playerUuid)

                val histories = virtualPlayer.histories.getOrElse(tick) { arrayListOf() }
                virtualPlayer.histories.set(tick, histories)

                histories.add(data)
            }
        }

        // 경계선 데이터 캐싱
        replay.recordDatas.forEach { tick, datas ->
            datas.filter {
                it is GameWorldBorderChangeData
            }.forEach { data ->
                virtualWorldBorder.histories.put(tick, arrayListOf(data))
            }
        }

        // 플레이어 텔레포트
        TeleportUtil.teleport(viewer, replay.startLocation)

        // 이벤트
        Bukkit.getPluginManager().callEvent(ReplayInitEvent(this))
    }

    /**
     * 재생
     */
    fun play() {
        if (isRunning()) error("Replay already running")

        replayTask = object : BukkitRunnable() {
            override fun run() {
                if (currentTick.toInt() % 20 == 0) { // TODO remove
                    BukkitBroadcaster.broadcast("§b리플레이: §f${(currentTick.toFloat() / 20).toLong()}§b초")
                }

                val toTick = currentTick.toLong()
                for (tick in (lastPlayedTick + 1)..toTick) {
                    val tickRecordDatas = replay.recordDatas.get(tick)
                    tickRecordDatas?.forEach { recordData ->
                        val recordDataReplayHandler = GameReplayApi.unsafe.recordDataManager.get(recordData.javaClass)
                        recordDataReplayHandler?.onPlay(recordData, viewer)
                    }

                    lastPlayedTick = tick
                }

                currentTick += playSpeed

                if (currentTick >= replay.endTick) {
                    pause()
                }
            }
        }.runTaskTimer(GameReplayPlugin.instance, 0L, 1L)

        // 이벤트
        Bukkit.getPluginManager().callEvent(ReplayPlayStartEvent(this))
    }

    /**
     * 정지
     */
    fun pause() {
        if (!isRunning()) error("Replay not running")

        replayTask?.cancel()
        replayTask = null
    }

//    /**
//     * 뒤로가기
//     */
//    fun goBack(tick: Long) {
//        for (i in 0 until tick) {
//            val tickRecordDatas = replay.recordDatas.get(currentTick.toLong())
//            tickRecordDatas?.forEach { recordData ->
//                val recordDataReplayHandler = GameReplayApi.unsafe.recordDataReplayHandlerManager.get(recordData.javaClass)
//                recordDataReplayHandler?.onReversePlay(this@ReplayPlayer, recordData as Nothing, currentTick.toLong())
//            }
//
//            currentTick--
//        }
//    }

    /**
     * 시간 이동
     */
    fun jumpTo(tick: Long) {
        if (tick < 0) error("jumpTo tick cannot lower than 0")
        if (tick > replay.endTick) error("jumpTo tick cannot higher than endTick")

        val beforeTick = currentTick
        currentTick = tick.toFloat()
        lastPlayedTick = tick - 1

        BukkitSyncScope.launch {
            viewer.sendTitle("", "")

            // 블럭
            virtualBlockManager.values().forEach {
                it.timeMachine(tick)
            }

            virtualBlockManager.showToBulk(virtualBlockManager.values().toList(), viewer)

            // 엔티티
            virtualEntityManager.values().forEach {
                it.timeMachine(beforeTick.toLong(), tick, viewer)
            }

            // 플레이어
            virtualPlayerManager.values().forEach {
                it.timeMachine(tick, viewer)
            }

            // 경계선
            virtualWorldBorder.timeMachine(tick, viewer)

            // 이벤트
            Bukkit.getPluginManager().callEvent(ReplayJumpToEvent(this@ReplayPlayer, beforeTick, tick))
        }
    }

    /**
     * 종료
     */
    fun exit() {
        isExit = true

        // 정지
        if (isRunning()) {
            pause()
        }

        // NPC 제거
        virtualPlayerManager.values().forEach { it.citizensNpc.destroy() }

        // 뷰어 텔레포트
        GameReplayApi.spawnConfig.spawnLocation?.let { spawnLocation -> TeleportUtil.teleport(viewer, spawnLocation) }

        // 이벤트
        Bukkit.getPluginManager().callEvent(ReplayExitEvent(this))
    }

}