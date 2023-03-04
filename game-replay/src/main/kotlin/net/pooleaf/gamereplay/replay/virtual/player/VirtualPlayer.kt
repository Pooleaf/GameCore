package net.pooleaf.gamereplay.replay.virtual.player

import net.citizensnpcs.api.npc.NPC
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.data.player.*
import net.pooleaf.gamereplay.replay.virtual.VirtualHistory
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

class VirtualPlayer(
    val uuid: UUID,
    val citizensNpc: NPC
) : VirtualHistory() {

    var isDefeated: Boolean = false

    var health: Double = 20.0

    val location: Location
        get() = citizensNpc.entity.location

    // 커스텀 데이터
    val etcDatas = HashMap<String, Any>()


    fun timeMachine(tick: Long, viewer: Player) {
        val datas = arrayListOf<RecordData>()

        getCurrentData(PlayerAnimationData::class.java, tick)?.let { datas.addAll(it) }
        getCurrentData(PlayerChatData::class.java, tick)?.let { datas.addAll(it) }
        getCurrentData(PlayerDamageData::class.java, tick)?.let { datas.addAll(it) }
        getLastData(PlayerEquipmentChangeData::class.java, tick)?.let { datas.add(it) }
        getLastData(PlayerHealthChangeData::class.java, tick)?.let { datas.add(it) }
        getLastData(PlayerMetaDataData::class.java, tick)?.let { datas.add(it) }
        getLastData(PlayerMoveData::class.java, tick)?.let { datas.add(it) }
        getCurrentData(PlayerTeleportData::class.java, tick)?.let { datas.addAll(it) }

        val lastHideTick = getLastDataTick(PlayerHideData::class.java, tick)
        val lastShowTick = getLastDataTick(PlayerShowData::class.java, tick)

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

        datas.forEach { data ->
            val playerHandler = GameReplayApi.unsafe.recordDataManager.get(data.javaClass) ?: return
            playerHandler.onPlay(data, viewer)
        }
    }

}