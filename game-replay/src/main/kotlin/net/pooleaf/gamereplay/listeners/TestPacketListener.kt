package net.pooleaf.gamereplay.listeners

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamereplay.GameReplayPlugin

class TestPacketListener: PacketAdapter(GameReplayPlugin.instance, ListenerPriority.NORMAL, PacketType.Play.Server.getInstance().values()) {

    override fun onPacketSending(event: PacketEvent) {

        if (event.packet.type == PacketType.Play.Server.SCOREBOARD_TEAM) return
        if (event.packet.type == PacketType.Play.Server.SCOREBOARD_OBJECTIVE) return
        if (event.packet.type == PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE) return
        if (event.packet.type == PacketType.Play.Server.SCOREBOARD_SCORE) return
        if (event.packet.type == PacketType.Play.Server.WORLD_PARTICLES) return

        println("player: ${event.player.name} / packetType: " + event!!.packet.type)

        val packet = event.packet
        if (event.packet.type == PacketType.Play.Server.BLOCK_CHANGE) {
            val position = packet.blockPositionModifier.read(0)
            val blockData = packet.blockData.read(0)


            println("position: ${position}")
            println("blockData: ${blockData}")
            println("type: ${blockData.type} / data: ${blockData.data}")

//            PacketPlayOutBlockChange
        }

        if (event.packet.type == PacketType.Play.Server.MULTI_BLOCK_CHANGE) {
            val chunk = packet.chunkCoordIntPairs.read(0)
            val multiBlockChangeInfo = packet.multiBlockChangeInfoArrays.read(0)

            println("chunk: ${chunk}")
            println("multiBlockChangeInfo: ${multiBlockChangeInfo.map { it.chunk }}")
        }

        if (event.packet.type == PacketType.Play.Server.MAP_CHUNK) {

        }


        if (packet.type == PacketType.Play.Server.ENTITY_METADATA) {
            val entityId = packet.integers.read(0)
            val entity = packet.getEntityModifier(event.player.world).read(0)
            val entityMetadata = packet.watchableCollectionModifier.read(0)

            println("entityId: ${entityId}")
            println("entity: ${entity}")
            println("entityMetadata: ${entityMetadata}")
        }


        if (packet.type == PacketType.Play.Server.ANIMATION) {
            val entityId = packet.integers.read(0)
            val entity = packet.getEntityModifier(event.player.world).read(0)
            val animation = packet.integers.read(1)

            println("entityId: ${entityId}")
            println("entity: ${entity}")
            println("animation: ${animation}")
        }

        if (packet.type == PacketType.Play.Server.WORLD_BORDER) {
            val worldBorderAction = packet.worldBorderActions.read(0)
            val b = packet.integers.read(0)
            val c = packet.doubles.read(0)
            val d = packet.doubles.read(1)
            val e = packet.doubles.read(2)
            val f = packet.doubles.read(3)
            val g = packet.longs.read(0)
            val h = packet.integers.read(1)
            val i = packet.integers.read(2)
        }


        if (event.packet.type == PacketType.Play.Server.MULTI_BLOCK_CHANGE) {
//            PacketPlayOutMultiBlockChange
        }
    }

    override fun onPacketReceiving(event: PacketEvent) {
        println("packetType: " + event!!.packet.type)



        if (event.packet.type == PacketType.Play.Client.BLOCK_DIG) {
            val digType = event.packet.playerDigTypes.read(0)
            val position = event.packet.blockPositionModifier.read(0)

            println("digType: ${digType}")
            println("position: ${position}")
        }
    }

}