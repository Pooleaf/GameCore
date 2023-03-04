package net.pooleaf.gamecore.listeners.control

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.weather.WeatherChangeEvent

/**
 * 자연적인 것들을 컨트롤하는 Listener
 */
class NatureControlListener: Listener {

    @EventHandler
    fun onWeatherChange(event: WeatherChangeEvent) {
        if (event.toWeatherState()) {
            event.isCancelled = true
        }
    }

}