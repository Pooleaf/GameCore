package net.pooleaf.gamereplay.replay

import com.comphenix.protocol.ProtocolLibrary
import net.pooleaf.core.modules.support.common.manager.AbstractManager
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.data.TpsData
import net.pooleaf.gamereplay.data.TpsDataReplayHandler
import net.pooleaf.gamereplay.data.block.*
import net.pooleaf.gamereplay.data.entity.*
import net.pooleaf.gamereplay.data.game.*
import net.pooleaf.gamereplay.data.player.*
import net.pooleaf.gamereplay.listeners.VirtualChunkLoadListener

class RecordDataManager : AbstractManager<Class<out RecordData>, RecordDataReplayHandler<out RecordData>>() {

    // Type, RecordData
    val recordDatas = hashMapOf<String, Class<out RecordData>>()


    fun registerHandlers() {
        // Block
        registerRecordData(BlockBreakData::class.java, BlockBreakDataReplayHandler())
        registerRecordData(BlockChangeData::class.java, BlockChangeDataReplayHandler())
        registerRecordData(BlockDamageData::class.java, BlockDamageDataReplayHandler())
        registerRecordData(EntityChangeBlockData::class.java, EntityChangeBlockDataReplayHandler())
        registerRecordData(EntityExplodeData::class.java, EntityExplodeDataReplayHandler())
        registerRecordData(BlockPlaceData::class.java, BlockPlaceDataReplayHandler())
        registerRecordData(MultiBlockChangeData::class.java, MultiBlockChangeDataReplayHandler())
        registerRecordData(UpdateSignData::class.java, UpdateSignDataReplayHandler())

        // Entity
        registerRecordData(CollectData::class.java, CollectDataReplayHandler())
        registerRecordData(EntityDestroyData::class.java, EntityDestroyDataReplayHandler())
        registerRecordData(EntityTeleportData::class.java, EntityTeleportDataReplayHandler())
        registerRecordData(EntityVelocityData::class.java, EntityVelocityDataReplayHandler())
        registerRecordData(ItemDespawnData::class.java, ItemDespawnDataReplayHandler())
        registerRecordData(ItemMetaDataData::class.java, ItemMetaDataDataReplayHandler())
        registerRecordData(SpawnEntityData::class.java, SpawnEntityDataReplayHandler())

        // Player
        registerRecordData(PlayerAnimationData::class.java, PlayerAnimationDataReplayHandler())
        registerRecordData(PlayerChatData::class.java, PlayerChatDataReplayHandler())
        registerRecordData(PlayerDamageData::class.java, PlayerDamageDataReplayHandler())
        registerRecordData(PlayerEquipmentChangeData::class.java, PlayerEquipmentChangeDataReplayHandler())
        registerRecordData(PlayerHealthChangeData::class.java, PlayerHealthChangeDataReplayHandler())
        registerRecordData(PlayerHideData::class.java, PlayerHideDataReplayHandler())
        registerRecordData(PlayerJoinData::class.java, PlayerJoinDataReplayHandler())
        registerRecordData(PlayerMetaDataData::class.java, PlayerMetaDataDataReplayHandler())
        registerRecordData(PlayerMoveData::class.java, PlayerMoveDataReplayHandler())
        registerRecordData(PlayerQuitData::class.java, PlayerQuitDataReplayHandler())
        registerRecordData(PlayerShowData::class.java, PlayerShowDataReplayHandler())
        registerRecordData(PlayerTeleportData::class.java, PlayerTeleportDataReplayHandler())

        // Game
        registerRecordData(GameEndData::class.java, GameEndDataReplayHandler())
        registerRecordData(GamePlayerDefeatData::class.java, GamePlayerDefeatDataReplayHandler())
        registerRecordData(GameWorldBorderChangeData::class.java, GameWorldBorderChangeDataReplayHandler())
        registerRecordData(TeamDefeatData::class.java, TeamDefeatDataReplayHandler())

        // ETC
        registerRecordData(TpsData::class.java, TpsDataReplayHandler())

        // Chunk
        ProtocolLibrary.getProtocolManager().addPacketListener(VirtualChunkLoadListener())
    }

    fun registerRecordData(recordDataClass: Class<out RecordData>, recordDataReplayHandler: RecordDataReplayHandler<out RecordData>) {
        set(recordDataClass, recordDataReplayHandler)
        recordDatas.put(recordDataClass.newInstance().type, recordDataClass)
    }

    fun getRecordDataClassByType(type: String): Class<out RecordData>? {
        return recordDatas.get(type)
    }

}