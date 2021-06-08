package scripts.bloodcrafter.tasks

import framework.Inventory
import framework.Task
import framework.moveAndTurnAndInteract
import org.powerbot.script.Area
import org.powerbot.script.Condition
import org.powerbot.script.Random
import org.powerbot.script.Tile
import org.powerbot.script.rt4.ClientContext
import scripts.bloodcrafter.Main

class MineBlocks(ctx: ClientContext) : Task<ClientContext>(ctx) {
    private val bloodAltarArea = listOf(
            Area(Tile(1742, 3854), Tile(1737, 3849)),
            Area(Tile(1705, 3860), Tile(1741, 3816)),
            Area(Tile(1744, 3857), Tile(1740, 3848))
    )
    private val darkAltarShortcutArea = Area(Tile(1757, 3878), Tile(1762, 3875))
    private val bloodAltarShortcutArea = Area(Tile(1738, 3851), Tile(1742, 3855))


    override fun validate(): Boolean {
        return !Inventory.contains("Dense essence block") || !ctx.inventory.isFull
    }

    override fun execute() {
        val playerInDarkAltarArea = Main.darkAltarArea.find { it.contains(Main.player) } != null
        val playerIsInBloodAltarArea = bloodAltarArea.find { it.contains(Main.player) } != null

        if (playerInDarkAltarArea) {
            darkAltarToMiningAreaShortcut()
            return
        }

        if (playerIsInBloodAltarArea) {
            bloodAltarToMineAreaShortcut()
            return
        }

        println("Mining dense runestone")
        val runeStoneList = ctx.objects.toStream().id(8975, 8981).nearest().list()
        if (runeStoneList.isEmpty()) {
            println("No dense runestones in the stream")
            return
        }

        val runeStone = runeStoneList.last()
        runeStone.bounds(Random.nextInt(20, 32), Random.nextInt(20, 32), Random.nextInt(20, 32), Random.nextInt(20, 32), Random.nextInt(20, 32), Random.nextInt(20, 32))
        if (runeStone.moveAndTurnAndInteract("Chip")) {
            Condition.sleep(750)
            Condition.wait ({ ctx.players.local().inMotion() && ctx.players.local().animation() != -1 }, 650, 3)
            Condition.wait({ ctx.players.local().animation() == -1 }, 1000, 200)
        }
    }

    private fun darkAltarToMiningAreaShortcut() {
        println("darkAltarToMiningAreaShortcut")
        val shortCutArea = Area(Tile(1765, 3877), Tile( 1756, 3867))
        val rocks = ctx.objects.toStream().within(shortCutArea).name("Rocks").first()

        if (!rocks.valid() && !darkAltarShortcutArea.contains(Main.player)) {
            ctx.movement.step(darkAltarShortcutArea.randomTile)
            return
        }

        rocks.bounds(Random.nextInt(20, 32), Random.nextInt(20, 32), Random.nextInt(20, 32), Random.nextInt(20, 32), Random.nextInt(20, 32), Random.nextInt(20, 32))
        if (rocks.moveAndTurnAndInteract("Climb", 15)) {
            Main.waitForAnimation()
        }
    }

    private fun bloodAltarToMineAreaShortcut() {
        println("bloodAltarToMineAreaShortcut")
        val shortCutArea = Area(Tile(1739, 3856), Tile(1747, 3848))
        val rocks = ctx.objects.toStream().within(shortCutArea).name("Rocks").nearest().first()

        if (!rocks.valid() && !bloodAltarShortcutArea.contains(Main.player)) {
            ctx.movement.step(bloodAltarShortcutArea.randomTile)
            return
        }

        rocks.bounds(Random.nextInt(20, 32), Random.nextInt(20, 32), Random.nextInt(20, 32), Random.nextInt(20, 32), Random.nextInt(20, 32), Random.nextInt(20, 32))
        if (rocks.moveAndTurnAndInteract("Climb")) {
            Main.fragmentsCharged = false
            Main.waitForAnimation()
        }
    }
}