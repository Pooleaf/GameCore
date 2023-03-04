package net.pooleaf.gamecore.map

interface GameMapFactory<T: GameMap> {

    fun createGameMap(): T

}