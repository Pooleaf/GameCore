package net.pooleaf.gamecore.supply

import net.pooleaf.gamecore.player.GamePlayer
import org.bukkit.Location

data class SupplyBlock(
    val supply: Supply,
    val location: Location
) {

    // 보급품을 받은 플레이어
    var usedBy: GamePlayer? = null

}