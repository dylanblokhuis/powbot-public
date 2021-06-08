package framework

import org.powerbot.script.Condition
import org.powerbot.script.rt4.ClientContext
import org.powerbot.script.rt4.Equipment
import org.powerbot.script.rt4.Game
import org.powerbot.script.rt4.Item
import java.util.regex.Pattern

object Gear {
    val ctx: ClientContext = ClientContext.ctx()
    var equippedRing: Item? = null;

    fun setEquippedRing() {
        val ctx: ClientContext = ClientContext.ctx()
        println("Checking equipped ring")
        if (ctx.bank.opened()) {
            println("Closing bank")
            ctx.bank.close()
            Condition.wait({ !ctx.bank.opened() }, 500, 3)
            return;
        }

        if (ctx.game.tab() != Game.Tab.EQUIPMENT) {
            println("Opening tab")
            ctx.game.tab(Game.Tab.EQUIPMENT)
            Condition.wait({ ctx.game.tab() == Game.Tab.EQUIPMENT}, 500, 3)
        } else {
            Condition.sleep(500)
            val item = ctx.equipment.itemAt(Equipment.Slot.RING)
            println("SCANNED ITEM $item")
            this.equippedRing = item
        }
    }

    fun contains(itemName: String, slot: Equipment.Slot): Boolean {
        if (slot == Equipment.Slot.RING) {
            if (equippedRing == null) return false
            return equippedRing!!.name().contains(itemName)
        } else {
            return ctx.equipment.itemAt(slot).name().contains(itemName)
        }
    }

    fun contains(itemName: Pattern, slot: Equipment.Slot): Boolean {
        if (slot == Equipment.Slot.RING) {
            if (equippedRing == null) return false

            return equippedRing!!.name().matches(itemName.toRegex())
        } else {
            return ctx.equipment.itemAt(slot).name().matches(itemName.toRegex())
        }
    }
}