package framework

import org.powerbot.script.rt4.ClientContext
import org.powerbot.script.Condition
import org.powerbot.script.rt4.Bank
import org.powerbot.script.rt4.Equipment
import org.powerbot.script.rt4.Game
import java.util.regex.Pattern

object Banking {
    val ctx: ClientContext = ClientContext.ctx()

    fun deposit(itemName: String, amount: Int? = null): Boolean {
        if (!ctx.bank.opened()) {
            open()
            return false
        }

        if (!ctx.inventory.toStream().name(itemName).first().valid()) {
            return true;
        }

        return if (amount == null) {
            ctx.bank.deposit(itemName, Bank.Amount.ALL)
        } else {
            ctx.bank.deposit(itemName, amount)
        }
    }

    fun deposit(itemName: Pattern, amount: Int? = null): Boolean {
        if (!ctx.bank.opened()) {
            open()
            return false
        }

        val item = ctx.inventory.toStream().name(itemName).first()

        if (!item.valid()) {
            return true
        }

        return if (amount == null) {
            ctx.bank.deposit(item.name(), Bank.Amount.ALL)
        } else {
            ctx.bank.deposit(item.name(), amount)
        }
    }

    fun withdraw(itemName: String, amount: Int? = null, noted: Boolean? = false): Boolean {
        if (!ctx.bank.opened()) {
            open()
            return false
        }

        if (!ctx.bank.toStream().name(itemName).first().valid()) {
            println("Stopping script because cannot find $itemName in bank")
            ctx.controller.stop()
            return false
        }

        if (noted == true && !ctx.bank.withdrawModeNoted()) {
            ctx.bank.withdrawModeNoted(true)
        } else if (noted == false && ctx.bank.withdrawModeNoted()) {
            ctx.bank.withdrawModeNoted(false)
        }

        return if (amount == null) {
            ctx.bank.withdraw(itemName, Bank.Amount.ALL)
        } else {
            ctx.bank.withdraw(itemName, amount)
        }
    }

    fun withdraw(itemName: Pattern, amount: Int? = null, noted: Boolean? = false): Boolean {
        if (!ctx.bank.opened()) {
            open()
            return false
        }

        if (!ctx.bank.toStream().name(itemName).first().valid()) {
            println("Stopping script because cannot find $itemName in bank")
            ctx.controller.stop()
            return false
        }

        if (noted == true && !ctx.bank.withdrawModeNoted()) {
            ctx.bank.withdrawModeNoted(true)
        } else if (noted == false && ctx.bank.withdrawModeNoted()) {
            ctx.bank.withdrawModeNoted(false)
        }

        val item = ctx.bank.toStream().name(itemName).first().name()

        return if (amount == null) {
            ctx.bank.withdraw(item, Bank.Amount.ALL)
        } else {
            ctx.bank.withdraw(item, amount)
        }
    }

    fun withdrawAndWear(itemName: String, itemType: Equipment.Slot): Boolean {
        if (!Gear.contains(itemName, itemType) && Inventory.contains(itemName))  {
            if (!ctx.bank.opened() && ctx.game.tab() != Game.Tab.INVENTORY) {
                ctx.game.tab(Game.Tab.INVENTORY)
                Condition.wait({ ctx.game.tab(Game.Tab.INVENTORY) }, 500, 3)
                return false
            }

            ctx.inventory.toStream().name(itemName).first().interact("Wear")
            Condition.wait({ ctx.equipment.toStream().name(itemName).first().valid() }, 350, 4)
            return true
        }

        if (!Gear.contains(itemName, itemType) && !Inventory.contains(itemName)) {
            withdraw(itemName, 1)
            return false
        }

        return false
    }

    fun withdrawAndWear(itemName: Pattern, itemType: Equipment.Slot): Boolean {
        if (!Gear.contains(itemName, itemType) && Inventory.contains(itemName))  {
            if (!ctx.bank.opened() && ctx.game.tab() != Game.Tab.INVENTORY) {
                ctx.game.tab(Game.Tab.INVENTORY)
                Condition.wait({ ctx.game.tab(Game.Tab.INVENTORY) }, 500, 3)
                return false
            }

            ctx.inventory.toStream().name(itemName).first().interact("Wear")
            Condition.wait({ ctx.equipment.toStream().name(itemName).first().valid() }, 350, 4)
            return true
        }

        if (!Gear.contains(itemName, itemType) && !Inventory.contains(itemName)) {
            withdraw(itemName, 1)
            return false
        }

        return false
    }

    fun open(): Boolean {
        val bankChest = ctx.objects.toStream().name("Bank chest").first()

        return when {
            !bankChest.valid() -> false
            bankChest.inViewport() && bankChest.interact("Use") -> {
                Condition.wait({ ctx.bank.opened() }, 1000, 5)
            }
            bankChest.tile().distanceTo(ctx.players.local()) > 4 -> {
                ctx.movement.step(bankChest)
                Condition.wait({ ctx.players.local().inMotion() }, 500, 3)
            }
            else -> {
                ctx.camera.turnTo(bankChest)
                false
            }
        }
    }
}