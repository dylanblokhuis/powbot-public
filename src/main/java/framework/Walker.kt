package framework

import org.powerbot.script.Random
import org.powerbot.script.Tile
import org.powerbot.script.rt4.ClientContext

// credits to whoever posted this on discord
class Walker(private val ctx: ClientContext) {
    private fun getNextTile(tilePath: Array<Tile>): Tile {
        val nextTile = ctx.movement.newTilePath(*tilePath).next()
        var index = 0
        val player = ctx.players.local()

        for (i in tilePath.indices.reversed()) {
            if (tilePath[i] == nextTile) {
                if (i + 1 <= tilePath.size - 1 && nextTile.distanceTo(player) < 3) {
                    index = i + 1
                    break
                }
                index = i
                break
            } else if (tilePath[i].distanceTo(player) < 8) {
                index = i
                break
            }
        }
        return tilePath[index]
    }

    fun walkPath(t: Array<Tile>): Boolean {
        val tile = getNextTile(t)
        val derivedTile = tile.derive(Random.nextInt(-1, 1), Random.nextInt(-1, 1))
        println("Walking")
        return ctx.movement.step(derivedTile)
    }
}