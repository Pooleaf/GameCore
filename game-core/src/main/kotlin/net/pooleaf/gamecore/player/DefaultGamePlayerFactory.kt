package net.pooleaf.gamecore.player

import java.util.*

class DefaultGamePlayerFactory: GamePlayerFactory<GamePlayer> {

    override fun createGamePlayer(uuid: UUID): GamePlayer {
        return GamePlayer(uuid)
    }

}