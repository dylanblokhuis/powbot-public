package framework.antipattern.techniques

import framework.antipattern.PatternType
import org.powerbot.script.Random
import org.powerbot.script.rt4.ClientContext

class CameraAntiPattern : PatternType {
    override fun execute(ctx: ClientContext): Boolean {
        println("Performing CameraAntiPattern")
        return ctx.camera.angle(Random.nextInt(0, 360));
    }
}