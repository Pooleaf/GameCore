package net.pooleaf.gamecore.map

class DefaultGameMapFactory: GameMapFactory<GameMap> {

    override fun createGameMap(): GameMap {
        return GameMap()
    }

}