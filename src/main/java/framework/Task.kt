package framework

import org.powerbot.script.rt4.ClientAccessor
import org.powerbot.script.rt4.ClientContext

abstract class Task<C : ClientContext>(ctx: C) : ClientAccessor(ctx) {
    abstract fun validate(): Boolean
    abstract fun execute()
}