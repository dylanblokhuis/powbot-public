package scripts.bloodcrafter.tasks

import framework.Inventory
import framework.Task
import framework.Walker
import framework.moveAndTurnAndInteract
import org.powerbot.script.Area
import org.powerbot.script.Condition
import org.powerbot.script.Tile
import org.powerbot.script.rt4.ClientContext
import scripts.bloodcrafter.Main
import scripts.bloodcrafter.Paths

class BindRunes(ctx: ClientContext) : Task<ClientContext>(ctx) {
    val altarArea = Area(Tile(1713, 3832), Tile(1721, 3825))

    override fun validate(): Boolean {
        if (!ctx.inventory.isFull) {
            return Inventory.contains("Dark essence fragments")
                    && !Inventory.contains("Dark essence block")
                    && Main.fragmentsCharged
        }

        return Inventory.contains("Dark essence fragments")
                && Inventory.contains("Dark essence block")
                && Main.fragmentsCharged
    }

    override fun execute() {
        val altar = ctx.objects.toStream().name("Blood Altar").first()

        if (!altar.valid()) {
            Walker(ctx).walkPath(Paths.darkAltarToBloodAltar)
            return
        }

        if (altar.tile().distanceTo(ctx.players.local()).toInt() > 20) {
            Walker(ctx).walkPath(Paths.darkAltarToBloodAltar)
            return
        }

        if (altar.moveAndTurnAndInteract("Bind")) {
            Condition.wait({ !Inventory.contains("Dark essence fragments") }, 1000, 5)
        }
    }
}