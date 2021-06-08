package framework.antipattern.techniques

import framework.antipattern.PatternType
import org.powerbot.script.Condition
import org.powerbot.script.Random
import org.powerbot.script.rt4.ClientContext

class DelayAntiPattern : PatternType {
    override fun execute(ctx: ClientContext): Boolean {
        println("Performing DelayAntiPattern")
        val time = Random.nextInt(750, 1025)
        return Condition.sleep(time) != 0
    }
}