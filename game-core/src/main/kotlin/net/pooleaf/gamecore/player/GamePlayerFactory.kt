package net.pooleaf.gamecore.player

import java.util.UUID

interface GamePlayerFactory<T: GamePlayer> {

    fun createGamePlayer(uuid: UUID): T

}