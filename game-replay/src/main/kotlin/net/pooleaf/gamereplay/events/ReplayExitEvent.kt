package net.pooleaf.gamereplay.events

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import net.pooleaf.gamereplay.replay.ReplayPlayer

class ReplayExitEvent(val replayPlayer: ReplayPlayer) : HandlerEvent() {
}