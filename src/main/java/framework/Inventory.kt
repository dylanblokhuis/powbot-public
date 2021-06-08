package framework

import org.powerbot.script.rt4.ClientContext
import org.powerbot.script.rt4.Item
import java.util.regex.Pattern

object Inventory {
    val ctx: ClientContext = ClientContext.ctx()

    fun contains(itemName: String, amount: Int? = null): Boolean {
        if (amount == null) {
            return ctx.inventory.toStream().name(itemName).first().valid()
        }

        val item = ctx.inventory.toStream().name(itemName).list()
        if (item.isEmpty()) {
            return false
        }

        return item.size >= amount
    }

    fun contains(itemName: Pattern, amount: Int? = null): Boolean {
        if (amount == null) {
            return ctx.inventory.toStream().name(itemName).first().valid()
        }

        val item = ctx.inventory.toStream().name(itemName).list()
        if (item.isEmpty()) {
            return false
        }

        return item.size >= amount
    }

    fun sortSnake(list: MutableList<Item>): MutableList<Item> {
        var items = list
        val leftSide = ArrayList<Item>()
        val rightSide = ArrayList<Item>()
        items.forEach { item ->
            if (item.valid()) {
                if (item.inventoryIndex() % 4 < 2) leftSide.add(item) else rightSide.add(item)
            }
        }

        items = ArrayList()
        items.addAll(leftSide)
        rightSide.reverse()
        items.addAll(rightSide)

        return items
    }

    fun sortTopBottom(list: MutableList<Item>): MutableList<Item> {
        var items = list
        val first = ArrayList<Item>()
        val second = ArrayList<Item>()
        val third = ArrayList<Item>()
        val fourth = ArrayList<Item>()
        items.forEach { item ->
            if (item.valid()) {
                when (item.inventoryIndex() % 4) {
                    0 -> first.add(item)
                    1 -> second.add(item)
                    2 -> third.add(item)
                    3 -> fourth.add(item)
                }
            }
        }

        items = ArrayList()

        second.reverse() // down
        fourth.reverse() // down

        items.addAll(first)
        items.addAll(second)
        items.addAll(third)
        items.addAll(fourth)

        return items
    }
}