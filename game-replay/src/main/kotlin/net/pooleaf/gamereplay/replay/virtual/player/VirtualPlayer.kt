package net.pooleaf.gamereplay.replay.virtual.player

import net.citizensnpcs.api.npc.NPC
import net.citizensnpcs.trait.GameModeTrait
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.data.datas.game.GamePlayerDefeatData
import net.pooleaf.gamereplay.data.datas.player.*
import net.pooleaf.gamereplay.replay.virtual.VirtualHistory
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.*

class VirtualPlayer(
    val uuid: UUID,
    val citizensNpc: NPC
) : VirtualHistory() {

    var isDefeated: Boolean = false

    var isOnline: Boolean = true

    var health: Double = 20.0

    val location: Location
        get() = citizensNpc.entity.location

    var isSpawned: Boolean = true

    // 커스텀 데이터
    val etcDatas = HashMap<String, Any>()


    fun spawnNpc(viewer: Player, location: Location = citizensNpc.storedLocation) {
        citizensNpc.spawn(location)
        citizensNpc.getOrAddTrait(GameModeTrait::class.java).gameMode = GameMode.CREATIVE
        (citizensNpc.entity as Player?)?.gameMode = GameMode.CREATIVE

        // 다른 플레이어들에게서 NPC 가리기
        Bukkit.getOnlinePlayers().filter { it != viewer }.forEach { it.hidePlayer(citizensNpc.entity as Player?) }

        isSpawned = true
    }
    
    fun despawnNpc() {
        citizensNpc.despawn()

        isSpawned = false
    }

    fun teleport(viewer: Player, location: Location) {
        despawnNpc()
        spawnNpc(viewer, location)
        citizensNpc.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN)
    }

    fun timeMachine(tick: Long, viewer: Player) {
        val datas = arrayListOf<RecordData>()


        // 가리기, 보이기 처리
        val lastHideTick = getLastDataTick(PlayerHideData::class.java, tick)
        val lastShowTick = getLastDataTick(PlayerShowData::class.java, tick)

        if (lastHideTick == null) {
            despawnNpc()
            spawnNpc(viewer)
        }

        if (lastHideTick != null && lastShowTick == null) {
            getLastData(PlayerHideData::class.java, tick)?.let { datas.add(it) }
        }

        if (lastHideTick != null && lastShowTick != null) {
            if (lastHideTick > lastShowTick) {
                getLastData(PlayerHideData::class.java, tick)?.let { datas.add(it) }
            } else if (lastHideTick < lastShowTick) {
                getLastData(PlayerShowData::class.java, tick)?.let { datas.add(it) }
            }
        }

        // 탈락 처리
        val lastDefeatTick = getLastDataTick(GamePlayerDefeatData::class.java, tick)
        if (lastDefeatTick == null || lastDefeatTick > tick) {
            isDefeated = false
        } else {
            isDefeated = true
        }

        // 온라인, 오프라인 처리
        val lastQuitTick = getLastDataTick(PlayerQuitData::class.java, tick)
        val lastJoinTick = getLastDataTick(PlayerJoinData::class.java, tick)

        if (lastQuitTick == null) {
            isOnline = true
        }

        if (lastQuitTick != null && lastJoinTick == null) {
            isOnline = false
        }

        if (lastQuitTick != null && lastJoinTick != null) {
            if (lastQuitTick > lastJoinTick) {
                isOnline = false
            } else if (lastQuitTick < lastJoinTick) {
                isOnline = true
            }
        }

        // NPC가 안보이는 문제가 있어 리스폰
        if (isSpawned) {
            despawnNpc()
            spawnNpc(viewer)
        }

        val lastTeleportTick = getLastDataTick(PlayerTeleportData::class.java, tick)
        val lastMoveTick = getLastDataTick(PlayerMoveData::class.java, tick)

        if (lastTeleportTick == null && lastMoveTick != null) {
            getLastData(PlayerMoveData::class.java, tick)?.let { datas.add(it) }
        }

        if (lastTeleportTick != null && lastMoveTick == null) {
            getLastData(PlayerTeleportData::class.java, tick)?.let { datas.add(it) }
        }

        if (lastTeleportTick != null && lastMoveTick != null) {
            if (lastTeleportTick > lastMoveTick) {
                getLastData(PlayerTeleportData::class.java, tick)?.let { datas.add(it) }
            } else if (lastTeleportTick < lastMoveTick) {
                getLastData(PlayerMoveData::class.java, tick)?.let { datas.add(it) }
            } else {
                getLastData(PlayerMoveData::class.java, tick)?.let { datas.add(it) }
                getLastData(PlayerTeleportData::class.java, tick)?.let { datas.add(it) }
            }
        }

        // 나머지 최근 데이터 처리
        getCurrentData(PlayerChatData::class.java, tick)?.let { datas.addAll(it) }
        getLastData(PlayerHealthChangeData::class.java, tick)?.let { datas.add(it) }

        if (isSpawned) {
            getCurrentData(PlayerAnimationData::class.java, tick)?.let { datas.addAll(it) }
            getCurrentData(PlayerDamageData::class.java, tick)?.let { datas.addAll(it) }
            getLastData(PlayerMetaDataData::class.java, tick)?.let { datas.add(it) }

            // 장비 처리
            val equipmentChangeDatas = histories.filterKeys { it <= tick }
                .toSortedMap()
                .values
                .flatten()
                .filterIsInstance<PlayerEquipmentChangeData>()

            equipmentChangeDatas.filter { it.equipmentType == 0 }.lastOrNull()?.let { datas.add(it) }
            equipmentChangeDatas.filter { it.equipmentType == 4 }.lastOrNull()?.let { datas.add(it) }
            equipmentChangeDatas.filter { it.equipmentType == 3 }.lastOrNull()?.let { datas.add(it) }
            equipmentChangeDatas.filter { it.equipmentType == 2 }.lastOrNull()?.let { datas.add(it) }
            equipmentChangeDatas.filter { it.equipmentType == 1 }.lastOrNull()?.let { datas.add(it) }
        }

        // 재생
        datas.forEach { data ->
            val replayHandler = GameReplayApi.unsafe.recordDataManager.get(data.javaClass) ?: return
            replayHandler.onPlay(data, viewer)
        }
    }

}