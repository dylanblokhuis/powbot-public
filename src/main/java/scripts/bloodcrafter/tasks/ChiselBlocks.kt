package scripts.bloodcrafter.tasks

import framework.Inventory
import framework.Task
import org.powerbot.script.Condition
import org.powerbot.script.rt4.ClientContext
import org.powerbot.script.rt4.Game
import org.powerbot.script.rt4.Item
import scripts.bloodcrafter.Main

class ChiselBlocks(ctx: ClientContext) : Task<ClientContext>(ctx) {
    override fun validate(): Boolean {
        if (Main.fragmentsCharged) {
            if (!ctx.inventory.isFull) {
                return Inventory.contains("Dark essence block")
            }

            return Inventory.contains("Dark essence block")
                    && !Inventory.contains("Dark essence fragments")
        }

        return Inventory.contains("Dark essence block")
    }

    override fun execute() {
        if (ctx.game.tab() != Game.Tab.INVENTORY) {
            ctx.game.tab(Game.Tab.INVENTORY)
        }

        val chisel = ctx.inventory.toStream().id(1755).first()
        val essenceBlocks = ctx.inventory.toStream().name("Dark essence block").list()

        /**
         * Prevent it from clicking the chisel when all blocks are already chiseled
         */
        if (essenceBlocks.size <= 1) return

        if (chisel.interact("Use")) {
            Condition.wait({ ctx.inventory.selectedItem() != Item.NIL }, 500, 3)
            if (essenceBlocks.last().click()) {
                Condition.wait({ ctx.inventory.selectedItem() == Item.NIL }, 500, 3)
            }
        }
    }
}