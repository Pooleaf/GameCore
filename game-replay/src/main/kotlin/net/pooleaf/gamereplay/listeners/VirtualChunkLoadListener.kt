package net.pooleaf.gamereplay.listeners

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamereplay.GameReplayApi
import org.bukkit.Bukkit

/**
 * 플레이어 청크 로딩 시 가상 블럭을 보내줍니다.
 */
class VirtualChunkLoadListener : PacketAdapter(GameCore.gamePlugin, PacketType.Play.Server.MAP_CHUNK, PacketType.Play.Server.MAP_CHUNK_BULK) {

    override fun onPacketSending(event: PacketEvent) {
        val player = event.player
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(player.uniqueId)
        if (replayPlayer == null) return

        val packet = event.packet

        when (packet.type) {
            PacketType.Play.Server.MAP_CHUNK -> {
                val chunkX = packet.integers.read(0)
                val chunkZ = packet.integers.read(1)

                Bukkit.getScheduler().runTaskLater(GameCore.gamePlugin, {
                    val virtualBlocks = replayPlayer.virtualBlockManager.getByChunk(chunkX, chunkZ)
                    replayPlayer.virtualBlockManager.showToBulk(virtualBlocks, player)
                }, 1L)
            }

            PacketType.Play.Server.MAP_CHUNK_BULK -> {
                val chunkXArray = packet.integerArrays.read(0)
                val chunkZArray = packet.integerArrays.read(1)

                Bukkit.getScheduler().runTaskLater(GameCore.gamePlugin, {
                    for (i in chunkXArray.indices) {
                        val chunkX = chunkXArray.get(i)
                        val chunkZ = chunkZArray.get(i)

                        val virtualBlocks = replayPlayer.virtualBlockManager.getByChunk(chunkX, chunkZ)
                        replayPlayer.virtualBlockManager.showToBulk(virtualBlocks, player)
                    }
                }, 1L)
            }
        }
    }

}