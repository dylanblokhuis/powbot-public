package scripts.bloodcrafter.tasks

import framework.Inventory
import framework.Task
import framework.Walker
import framework.moveAndTurnAndInteract
import org.powerbot.script.Area
import org.powerbot.script.Condition
import org.powerbot.script.Random
import org.powerbot.script.Tile
import org.powerbot.script.rt4.ClientContext
import scripts.bloodcrafter.Main
import scripts.bloodcrafter.Paths

class ConvertBlocks(ctx: ClientContext) : Task<ClientContext>(ctx) {
    private val agilityArea = Area(Tile(1765, 3877), Tile( 1756, 3867))
    private val altarArea = Area(Tile(1713, 3885), Tile(1720, 3878))
    private val obstacleTile = Tile(1761, 3869, 0);

    override fun validate(): Boolean {
        return Inventory.contains("Dense essence block")
                && ctx.inventory.isFull
    }

    override fun execute() {
        val playerInDarkAltarArea = Main.darkAltarArea.find { it.contains(Main.player) } != null

        // player is not in the area, so we need to go the shortcut
        if (!playerInDarkAltarArea) {
            mineAreaToDarkAltarShortcut()
            return
        }

        // player has passed the shortcut, so we can go to the Dark Altar to convert the blocks
        val altar = ctx.objects.toStream().name("Dark Altar").first()
        if (altar.valid() && altar.tile().distanceTo(Main.player) > 20) {
            ctx.movement.step(altarArea.randomTile)
            return
        }

        if (!altar.valid()) {
            ctx.movement.step(altarArea.randomTile)
            return
        }

        val hasFragmentBeforeInteracting = Inventory.contains("Dark essence fragments")

        altar.bounds(Random.nextInt(20, 32), Random.nextInt(20, 32), Random.nextInt(20, 32), Random.nextInt(20, 32), Random.nextInt(20, 32), Random.nextInt(20, 32))
        if (altar.moveAndTurnAndInteract("Venerate")) {
            Condition.wait({ Inventory.contains("Dark essence block") }, 750, 10)
            if (hasFragmentBeforeInteracting) Main.fragmentsCharged = true
        }
    }

    private fun mineAreaToDarkAltarShortcut() {
        println("mineAreaToDarkAltarShortcut")
        val rocks = ctx.objects.toStream().within(agilityArea).name("Rocks").first()
        if (!rocks.valid()) return

        if (Main.player.tile().distanceTo(obstacleTile) < 10) {
            rocks.bounds(Random.nextInt(20, 32), Random.nextInt(20, 32), Random.nextInt(20, 32), Random.nextInt(20, 32), Random.nextInt(20, 32), Random.nextInt(20, 32))
            if (rocks.moveAndTurnAndInteract("Climb", 15)) {
                Main.waitForAnimation()
            }
        } else {
            Walker(ctx).walkPath(Paths.miningAreaToDarkAltarShortcut)
        }
    }
}