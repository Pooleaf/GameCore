package net.pooleaf.gamecore.events.player

import net.pooleaf.core.modules.eventsupport.bukkit.events.CancellableEvent
import net.pooleaf.gamecore.player.GamePlayer
import net.pooleaf.gamecore.startitem.StartItem

/**
 * 플레이어 시작 아이템 지급 시 호출됩니다.
 * 시작 아이템 지급을 완전히 취소하려면 [gamePlayer.isReceiveStartItems]를 수정해야합니다.
 */
class GamePlayerStartItemReceiveEvent(
    val gamePlayer: GamePlayer,
    var startItem: StartItem
): CancellableEvent()