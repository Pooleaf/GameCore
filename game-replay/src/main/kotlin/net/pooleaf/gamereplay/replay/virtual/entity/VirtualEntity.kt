package net.pooleaf.gamereplay.replay.virtual.entity

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.data.entity.*
import net.pooleaf.gamereplay.replay.virtual.VirtualHistory
import org.bukkit.entity.Player

data class VirtualEntity(
    val entityId: Int
) : VirtualHistory() {

    fun isSpawned(tick: Long): Boolean {
        val spawnEntityDataTick = histories.filter { it.value.filterIsInstance<SpawnEntityData>().isNotEmpty() }.firstNotNullOfOrNull { it.key }
        val entityDestroyDataTick = histories.filter { it.value.filterIsInstance<EntityDestroyData>().isNotEmpty() }.firstNotNullOfOrNull { it.key }

        if (spawnEntityDataTick == null && entityDestroyDataTick == null) return true
        if (spawnEntityDataTick != null && entityDestroyDataTick == null) return spawnEntityDataTick <= tick
        if (spawnEntityDataTick == null && entityDestroyDataTick != null) return tick < entityDestroyDataTick
        if (spawnEntityDataTick != null && entityDestroyDataTick != null) return spawnEntityDataTick <= tick && tick < entityDestroyDataTick

        return false
    }

    fun spawn(viewer: Player) {
        val spawnEntityData = histories.values.flatten()
            .filterIsInstance<SpawnEntityData>()
            .firstOrNull() ?: return
        val spawnEntityHandler = GameReplayApi.unsafe.recordDataManager.get(SpawnEntityData::class.java) ?: return

        spawnEntityHandler.onPlay(spawnEntityData, viewer)
    }

    fun destroy(viewer: Player) {
        var entityDestroyData = histories.values.flatten()
            .filterIsInstance<EntityDestroyData>()
            .firstOrNull()
        val entityDestroyHandler = GameReplayApi.unsafe.recordDataManager.get(EntityDestroyData::class.java) ?: return

        if (entityDestroyData == null) {
            entityDestroyData = EntityDestroyData()
            entityDestroyData.entityIds = arrayOf(entityId)

            entityDestroyHandler.onPlay(entityDestroyData, viewer)
        } else {
            entityDestroyHandler.onPlay(entityDestroyData, viewer)
        }
    }

    fun teleport(tick: Long, viewer: Player) {
        val entityTeleportData = histories.filterKeys { it <= tick }
            .filterValues { it.filterIsInstance<EntityTeleportData>().isNotEmpty() }
            .maxByOrNull { it.key }
            ?.value
            ?.filterIsInstance<EntityTeleportData>()
            ?.firstOrNull() ?: return
        val entityTeleportHandler = GameReplayApi.unsafe.recordDataManager.get(EntityTeleportData::class.java) ?: return

        entityTeleportHandler.onPlay(entityTeleportData, viewer)
    }

    fun timeMachine(beforeTick: Long, newTick: Long, viewer: Player) {
        val beforeSpawned = isSpawned(beforeTick)
        val newSpawned = isSpawned(newTick)

        // 스폰
        if (!beforeSpawned && newSpawned) {
            spawn(viewer)
            teleport(newTick, viewer)
        }
        // 디스폰
        else if (beforeSpawned && !newSpawned) {
            destroy(viewer)
        }

        val datas = arrayListOf<RecordData>()

        getCurrentData(CollectData::class.java, newTick)?.let { datas.addAll(it) }
        getCurrentData(EntityDestroyData::class.java, newTick)?.let { datas.addAll(it) }
        getLastData(EntityTeleportData::class.java, newTick)?.let { datas.add(it) }
        getLastData(EntityVelocityData::class.java, newTick)?.let { datas.add(it) }
        getLastData(ItemMetaDataData::class.java, newTick)?.let { datas.add(it) }

        datas.forEach { data ->
            val playerHandler = GameReplayApi.unsafe.recordDataManager.get(data.javaClass) ?: return
            playerHandler.onPlay(data, viewer)
        }
    }

}