package framework.antipattern

import org.powerbot.script.rt4.ClientContext

interface PatternType {
    fun execute(ctx: ClientContext): Boolean
}