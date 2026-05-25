package net.pooleaf.gamecore.map

import net.pooleaf.core.modules.support.common.logger.Logger
import org.bukkit.Material
import org.bukkit.block.Block

/**
 * NMS [net.minecraft.server.v1_8_R3.MaterialMapColor] 룩업 헬퍼.
 * vanilla 마인크래프트 지도가 사용하는 색 테이블을 그대로 가져와
 * Material/Block을 128x128 지도 팔레트 바이트로 변환한다.
 */
object NmsMapColor {

    private const val BRIGHTNESS = 2

    private val fallbackByte: Byte = 0

    private val materialCache = HashMap<Material, Byte>()

    fun get(material: Material, data: Byte = 0): Byte {
        materialCache[material]?.let { return it }

        val computed = try {
            computeColorByte(material, data)
        } catch (e: Throwable) {
            Logger.warning("지도 색상 룩업 실패: ${material.name} (${e.javaClass.simpleName}: ${e.message})")
            fallbackByte
        }

        materialCache[material] = computed
        return computed
    }

    fun get(block: Block): Byte {
        return get(block.type, block.data)
    }

    private fun computeColorByte(material: Material, data: Byte): Byte {
        val craftMagicNumbers = Class.forName("org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers")
        val getBlockMethod = craftMagicNumbers.getMethod("getBlock", Material::class.java)
        val nmsBlock = getBlockMethod.invoke(null, material) ?: return fallbackByte

        val nmsBlockClass = Class.forName("net.minecraft.server.v1_8_R3.Block")
        val fromLegacyData = nmsBlockClass.getMethod("fromLegacyData", Int::class.javaPrimitiveType)
        val iBlockData = fromLegacyData.invoke(nmsBlock, data.toInt() and 0x0F) ?: return fallbackByte

        val iBlockDataClass = Class.forName("net.minecraft.server.v1_8_R3.IBlockData")
        val getBlock = iBlockDataClass.getMethod("getBlock")
        val blockOfData = getBlock.invoke(iBlockData) ?: return fallbackByte

        val getMapColor = nmsBlockClass.getDeclaredMethod("g", iBlockDataClass)
        getMapColor.isAccessible = true
        val mapColor = getMapColor.invoke(blockOfData, iBlockData) ?: return fallbackByte

        val mapColorClass = Class.forName("net.minecraft.server.v1_8_R3.MaterialMapColor")
        val colorIndexField = mapColorClass.getField("M")
        val colorIndex = colorIndexField.getInt(mapColor)

        return (colorIndex * 4 + BRIGHTNESS).toByte()
    }
}
