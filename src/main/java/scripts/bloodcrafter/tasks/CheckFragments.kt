package scripts.bloodcrafter.tasks

import framework.Task
import org.powerbot.script.Condition
import org.powerbot.script.rt4.ClientContext
import org.powerbot.script.rt4.Game
import scripts.bloodcrafter.Main

class CheckFragments(ctx: ClientContext) : Task<ClientContext>(ctx) {
    override fun validate(): Boolean {
        return !Main.isFragmentChecked
    }

    override fun execute() {
        val countChatMessage = ctx.widgets.widget(193).component(2)
        if (countChatMessage.valid()) {
            val charges = countChatMessage.text().filter { it.isDigit() }
            if (charges.toInt() >= 104) {
                Main.fragmentsCharged = true
                Main.isFragmentChecked = true
            } else {
                Main.isFragmentChecked = true
            }
        }

        if (ctx.game.tab() != Game.Tab.INVENTORY) {
            ctx.game.tab(Game.Tab.INVENTORY)
        }

        val fragments = ctx.inventory.toStream().name("Dark essence fragments").first()
        if (!fragments.valid()) {
            Main.isFragmentChecked = true
            return
        }

        if (fragments.interact("Count")) {
            Condition.wait { ctx.widgets.widget(193).component(2).valid() }
        }
    }
}