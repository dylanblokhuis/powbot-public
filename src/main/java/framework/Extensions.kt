package framework

import org.powerbot.script.Condition
import org.powerbot.script.InteractiveEntity
import org.powerbot.script.Random
import org.powerbot.script.rt4.ClientContext

@JvmName("moveAndTurnAndInteract")
fun InteractiveEntity.moveAndTurnAndInteract(action: String, walkToIfDistanceGreaterThan: Int = 10): Boolean {
    val ctx = ClientContext.ctx()

    when {
        !this.valid() -> {
            println("[moveAndTurnAndInteract] Not interacting because it's invalid")
            return false
        }
        this.inViewport() -> {
            println("[moveAndTurnAndInteract] Clicking $action")
            val result = this.interact(action)
            if (result) {
                Condition.sleep(500)
                Condition.wait({ ctx.players.local().animation() == -1 }, 450, 3)
            } else {
                ctx.camera.turnTo(this)
            }
            return result
        }
        ctx.players.local().tile().distanceTo(this) > walkToIfDistanceGreaterThan -> {
            println("[moveAndTurnAndInteract] Walking to")
            val tile = this.tile().derive(Random.nextInt(1, 3), Random.nextInt(1,3))
            ctx.movement.step(tile)
            Condition.wait({ ctx.players.local().inMotion() }, 1000, 10)
            return false
        }
        else -> {
            println("moveAndTurnAndInteract] Turning camera")
            ctx.camera.turnTo(this)
            return false
        }
    }
}

@JvmName("moveTo")
fun InteractiveEntity.moveTo(walkToIfDistanceGreaterThan: Int = 10): Boolean {
    val ctx = ClientContext.ctx()

    return when {
        !this.valid() -> {
            println("[moveTo] Not doing anything because it's invalid")
            false
        }
        ctx.players.local().tile().distanceTo(this) > walkToIfDistanceGreaterThan -> {
            println("[moveTo] Walking to")
            val tile = this.tile().derive(Random.nextInt(1, 3), Random.nextInt(1,3))
            ctx.movement.step(tile)
            Condition.wait({ ctx.players.local().inMotion() }, 1000, 10)
            false
        }
        else -> true
    }
}



