package scripts.bloodcrafter

import framework.Inventory
import framework.Task
import framework.antipattern.AntiPatternManager
import framework.formatTime
import framework.getPerHour
import framework.ui.Dialog
import org.powerbot.script.*
import org.powerbot.script.rt4.ClientContext
import org.powerbot.script.rt4.Constants
import org.powerbot.script.rt4.Player
import scripts.bloodcrafter.tasks.*
import java.awt.Color
import java.awt.Font
import java.awt.Graphics

@Script.Manifest(name = "Arceuus crafter", description = "Crafts blood runes in arceuus", version = "1.0.0", mobileReady = true)
class Main : PollingScript<ClientContext>(), PaintListener {
    private val antiPatternManager = AntiPatternManager(ctx)
    private var taskList: MutableList<Task<ClientContext>> = arrayListOf()
    private var startXp = ctx.skills.experience(Constants.SKILLS_RUNECRAFTING)
    private val helveticaFont = Font("Helvetica", Font.BOLD, 24)
    private var lastTask: String? = null
    private var taskRepeated = 0

    companion object {
        var isFragmentChecked = true
        var fragmentsCharged = false
        var player: Player = ClientContext.ctx().players.local()
        val darkAltarArea = arrayOf(
            Area(Tile(1765, 3881), Tile(1740, 3874)),
            Area(Tile(1740, 3875), Tile(1755, 3870)),
            Area(Tile(1702, 3904), Tile(1749, 3860)),
            Area(Tile(1747, 3865), Tile(1751, 3872)),
            Area(Tile(1756, 3871), Tile(1758, 3875))
        )

        fun waitForAnimation() {
            val ctx = ClientContext.ctx();
            Condition.sleep(750)
            Condition.wait ({ ctx.players.local().inMotion() && ctx.players.local().animation() != -1 }, 650, 3)
            Condition.wait ({ !ctx.players.local().inMotion() && ctx.players.local().animation() == -1 }, 1000, 10);
        }
    }

    override fun poll() {
        player = ctx.players.local()

        if (antiPatternManager.ready()) {
            antiPatternManager.perform()
        }

        if (ctx.movement.energyLevel() > Random.nextInt(20, 40) && !ctx.movement.running()) {
            ctx.movement.running(true)
        }

        // CheckFragments needs a chat message, so it can't be skipped
        if (ctx.chat.canContinue() && isFragmentChecked) {
            ctx.chat.clickContinue()
            Condition.wait({ !ctx.chat.canContinue() }, 350,4)
            return
        }

        val task = taskList.find { it.validate() }
        if (task == null) {
            println("No tasks validated, not doing anything...")
            return
        }

        println(task.javaClass.name)
        if (lastTask == task.javaClass.name) taskRepeated++ else taskRepeated = 0
        lastTask = task.javaClass.name
        if (taskRepeated >= 50) {
            ctx.controller.stop()
            Dialog.show(this.manifest.name, "Stopping because $lastTask repeated 50 times.")
            return
        }

        println("Task $lastTask count: $taskRepeated")
        task.execute()
    }

    override fun start() {
        println("Starting ${this.manifest.name}")
        ctx.properties["randomevents.disable"] = "true"

        if (Inventory.contains("Dark essence fragments")) {
            isFragmentChecked = false
        }

        if (!Inventory.contains(/* chisel */1755)) {
            ctx.controller.stop()
            Dialog.show(this.manifest.name,  "You need a chisel to use this script.")
            return
        }

        taskList.add(CheckFragments(ctx))
        taskList.add(ChiselBlocks(ctx))
        taskList.add(ConvertBlocks(ctx))
        taskList.add(BindRunes(ctx))
        taskList.add(MineBlocks(ctx))
    }

    override fun repaint(g: Graphics) {
        val currXp = ctx.skills.experience(Constants.SKILLS_RUNECRAFTING)
        val gainedXp = currXp - startXp
        val currentLevel = ctx.skills.level(Constants.SKILLS_RUNECRAFTING)
        val expLeft = ctx.skills.experienceAt(currentLevel + 1) - currXp

        // Info text
        g.font = helveticaFont
        g.color = Color(255,99,71, 255)
        g.drawString(this.manifest.name, 12, 65)
        g.drawString("Time running: ${formatTime(this.runtime.toInt() / 1000)}", 12, 95)
        g.drawString("RC XP gained: $gainedXp (${getPerHour(gainedXp, this.runtime)}/ph)", 12, 125)

        if (currentLevel != 99) {
            g.drawString("RC Level: $currentLevel", 12, 155)
            g.drawString("XP. left: $expLeft", 12, 185)
        }
        if (lastTask != null) {
            g.drawString("Current task: ${lastTask!!.replace("scripts.bloodcrafter.tasks.", "")} ($taskRepeated)", 12, 215)
        }
    }
}