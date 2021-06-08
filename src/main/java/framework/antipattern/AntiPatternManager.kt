package framework.antipattern

import framework.antipattern.techniques.CameraAntiPattern
import framework.antipattern.techniques.DelayAntiPattern
import org.powerbot.script.Random
import org.powerbot.script.rt4.ClientAccessor
import org.powerbot.script.rt4.ClientContext

/**
 * credits to https://github.com/DerkSchooltink/runemate-bots
 */
class AntiPatternManager<C : ClientContext>(ctx: C) : ClientAccessor(ctx) {
    private val patternTypeList = mutableListOf<PatternType>()
    private var performedPattern: PatternType? = DelayAntiPattern()
    private var interval: Int = Random.nextInt(110000, 220000)

    init {
        patternTypeList.add(DelayAntiPattern())
        patternTypeList.add(CameraAntiPattern())
    }

    fun ready(): Boolean {
        return ctx.controller.script().runtime >= interval
    }

    fun perform(): Boolean {
        return patternTypeList.filter { it != performedPattern }.shuffled().first().also {
            println("[AntiPattern] Executing ${it.javaClass.name}")
            reset()
        }.also {
            performedPattern = it
        }.execute(ctx)
    }

    private fun reset(): Boolean {
        interval = ctx.controller.script().runtime.toInt() + Random.nextInt(110000, 220000)
        return true
    }
}