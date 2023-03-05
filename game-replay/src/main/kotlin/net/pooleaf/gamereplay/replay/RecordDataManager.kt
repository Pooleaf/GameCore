package net.pooleaf.gamereplay.replay

import com.comphenix.protocol.ProtocolLibrary
import net.pooleaf.core.modules.support.common.manager.AbstractManager
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.data.datas.TpsData
import net.pooleaf.gamereplay.data.datas.block.*
import net.pooleaf.gamereplay.data.datas.entity.*
import net.pooleaf.gamereplay.data.datas.game.GameEndData
import net.pooleaf.gamereplay.data.datas.game.GamePlayerDefeatData
import net.pooleaf.gamereplay.data.datas.game.GameWorldBorderChangeData
import net.pooleaf.gamereplay.data.datas.game.TeamDefeatData
import net.pooleaf.gamereplay.data.datas.player.*
import net.pooleaf.gamereplay.data.records.block.BlockChangeDataRecordListener
import net.pooleaf.gamereplay.data.records.block.MultiBlockChangeDataRecordListener
import net.pooleaf.gamereplay.data.records.entity.*
import net.pooleaf.gamereplay.data.records.player.PlayerMetaDataDataRecordListener
import net.pooleaf.gamereplay.data.replays.TpsDataReplayHandler
import net.pooleaf.gamereplay.data.replays.block.*
import net.pooleaf.gamereplay.data.replays.entity.*
import net.pooleaf.gamereplay.data.replays.game.GameEndDataReplayHandler
import net.pooleaf.gamereplay.data.replays.game.GamePlayerDefeatDataReplayHandler
import net.pooleaf.gamereplay.data.replays.game.GameWorldBorderChangeDataReplayHandler
import net.pooleaf.gamereplay.data.replays.game.TeamDefeatDataReplayHandler
import net.pooleaf.gamereplay.data.replays.player.*
import net.pooleaf.gamereplay.listeners.TestPacketListener
import net.pooleaf.gamereplay.listeners.VirtualChunkLoadListener

class RecordDataManager : AbstractManager<Class<out RecordData>, RecordDataReplayHandler<out RecordData>>() {

    // Type, RecordData
    val recordDatas = hashMapOf<String, Class<out RecordData>>()


    fun registerRecordListeners() {
        ProtocolLibrary.getProtocolManager().addPacketListener(TestPacketListener())

        // Block
        ProtocolLibrary.getProtocolManager().addPacketListener(BlockChangeDataRecordListener())
        ProtocolLibrary.getProtocolManager().addPacketListener(MultiBlockChangeDataRecordListener())

        // Entity
        ProtocolLibrary.getProtocolManager().addPacketListener(CollectDataRecordListener())
        ProtocolLibrary.getProtocolManager().addPacketListener(EntityDestroyDataRecordListener())
        ProtocolLibrary.getProtocolManager().addPacketListener(EntityHeadRotationDataRecordListener())
        ProtocolLibrary.getProtocolManager().addPacketListener(EntityMetaDataDataRecordListener())
        ProtocolLibrary.getProtocolManager().addPacketListener(EntityTeleportDataRecordListener())
        ProtocolLibrary.getProtocolManager().addPacketListener(EntityVelocityDataRecordListener())
        ProtocolLibrary.getProtocolManager().addPacketListener(ItemMetaDataDataRecordListener())
        ProtocolLibrary.getProtocolManager().addPacketListener(RelEntityMoveDataRecordListener())
        ProtocolLibrary.getProtocolManager().addPacketListener(RelEntityMoveLookDataRecordListener())
        ProtocolLibrary.getProtocolManager().addPacketListener(SpawnEntityDataRecordListener())

        // Player
        ProtocolLibrary.getProtocolManager().addPacketListener(PlayerMetaDataDataRecordListener())
    }

    fun registerReplayHandlers() {
        // Block
        registerRecordData(BlockBreakData::class.java, BlockBreakDataReplayHandler())
        registerRecordData(BlockChangeData::class.java, BlockChangeDataReplayHandler())
        registerRecordData(BlockDamageData::class.java, BlockDamageDataReplayHandler())
        registerRecordData(EntityChangeBlockData::class.java, EntityChangeBlockDataReplayHandler())
        registerRecordData(ExplodeData::class.java, ExplodeDataReplayHandler())
        registerRecordData(BlockPlaceData::class.java, BlockPlaceDataReplayHandler())
        registerRecordData(MultiBlockChangeData::class.java, MultiBlockChangeDataReplayHandler())
        registerRecordData(UpdateSignData::class.java, UpdateSignDataReplayHandler())

        // Entity
        registerRecordData(CollectData::class.java, CollectDataReplayHandler())
        registerRecordData(EntityDestroyData::class.java, EntityDestroyDataReplayHandler())
        registerRecordData(EntityHeadRotationData::class.java, EntityHeadRotationDataReplayHandler())
        registerRecordData(EntityMetaDataData::class.java, EntityMetaDataDataReplayHandler())
        registerRecordData(EntityTeleportData::class.java, EntityTeleportDataReplayHandler())
        registerRecordData(EntityVelocityData::class.java, EntityVelocityDataReplayHandler())
        registerRecordData(ItemDespawnData::class.java, ItemDespawnDataReplayHandler())
        registerRecordData(ItemMetaDataData::class.java, ItemMetaDataDataReplayHandler())
        registerRecordData(RelEntityMoveData::class.java, RelEntityMoveDataReplayHandler())
        registerRecordData(RelEntityMoveLookData::class.java, RelEntityMoveLookDataReplayHandler())
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