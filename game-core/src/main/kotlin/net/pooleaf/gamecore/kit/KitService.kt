package net.pooleaf.gamecore.kit

import net.pooleaf.core.modules.annoconfig.AnnoConfigModule
import net.pooleaf.gamecore.GameCore
import java.io.File

class KitService {

    val kitFolder = File(GameCore.gamePlugin.dataFolder, "kit")


    /**
     * 킷을 저장합니다.
     */
    fun saveKitConfig(kit: Kit) {
        kitFolder.mkdirs()

        val file = File(kitFolder, "${kit.name}.yml")
        AnnoConfigModule.save(file, kit)
    }

    /**
     * 모든 킷을 저장합니다.
     */
    fun saveKitConfigs() {
        GameCore.unsafe.kitManager.values().forEach { saveKitConfig(it) }
    }

    /**
     * 킷을 불러옵니다.
     */
    fun loadKitConfig(name: String) {
        val kit = GameCore.unsafe.kitManager.get(name) ?: Kit()
        kit.name = name
        GameCore.unsafe.kitManager.set(name, kit)

        val file = File(kitFolder, "${kit.name}.yml")
        AnnoConfigModule.load(file, kit)
    }

    /**
     * 모든 킷을 불러옵니다.
     */
    fun loadKitConfigs() {
        kitFolder.mkdirs()

        kitFolder.listFiles().filter { it.name.endsWith(".yml") }
            .forEach { loadKitConfig(it.name.substringBefore(".yml")) }
    }

    /**
     * 킷을 삭제합니다.
     */
    fun deleteKitConfig(name: String) {
        val file = File(kitFolder, "${name}.yml")
        if (file.exists()) {
            file.delete()
        }
    }

    /**
     * 모든 킷을 삭제합니다.
     */
    fun deleteKitConfigs() {
        GameCore.unsafe.kitManager.values().forEach { deleteKitConfig(it.name) }
    }

}