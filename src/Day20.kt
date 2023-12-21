
import kotlin.collections.ArrayDeque
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableMap
import kotlin.collections.all
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.filterIsInstance
import kotlin.collections.filterValues
import kotlin.collections.forEach
import kotlin.collections.isNotEmpty
import kotlin.collections.map
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.collections.sumOf

private const val BROADCASTER = "broadcaster"

fun main() {

    fun parseInput(input: List<String>): Map<String, ModuleNode> {
        val lookupByName: MutableMap<String, ModuleNode> = mutableMapOf()
        input.forEach { line ->
            val (typeAndName, outputs) = line.split("->")

            var node : ModuleNode? = null
            var moduleName : String? = null
            if (typeAndName.startsWith("%")) {
                moduleName = typeAndName.substringAfter('%').trim()
                val defaultModule = Flipflop(name = moduleName)
                val defaultModuleNode = ModuleNode(defaultModule)
                defaultModule.enclosingModuleNode = defaultModuleNode
                node = lookupByName.getOrDefault(moduleName, defaultModuleNode)
                if (node.module is Placeholder) {
                    //replace
                    node.module = defaultModule
                }
                lookupByName[moduleName] = node
            } else if (typeAndName.startsWith("&")) {
                moduleName = typeAndName.substringAfter('&').trim()
                val defaultModule = Conjunction(name = moduleName)
                val defaultModuleNode = ModuleNode(defaultModule)
                defaultModule.enclosingModuleNode = defaultModuleNode
                node = lookupByName.getOrDefault(moduleName, defaultModuleNode)
                if (node.module is Placeholder) {
                    //replace
                    node.module = defaultModule
                }
                lookupByName[moduleName] = node

            } else if (typeAndName.trim().equals(BROADCASTER)) {
                moduleName = BROADCASTER
                val defaultModule = Broadcast(name = moduleName)
                val defaultModuleNode = ModuleNode(defaultModule)
                defaultModule.enclosingModuleNode = defaultModuleNode
                node = lookupByName.getOrDefault(moduleName, defaultModuleNode)
                if (node.module is Placeholder) {
                    //replace
                    node.module = defaultModule
                }
                lookupByName[moduleName] = node
            } else {
                throw RuntimeException("Unexpected :: ${typeAndName}")
            }

            //handle outputs
            node.module.outputs = outputs.split(",")
                .map { it.trim() }
                .map { output ->
                    val placeholderNode = lookupByName.getOrDefault(output, ModuleNode(Placeholder(name = output)))
                    lookupByName[output] = placeholderNode

                    placeholderNode
                }
        }

        //scan over the conjunction modules and update the inputs to them
        lookupByName.values.map {
             it.module
        }.filterIsInstance<Conjunction>().forEach {conjunction: Conjunction ->
            lookupByName.filterValues { moduleNode ->
                moduleNode.module.outputs.map { it.module }.contains(conjunction)
            }.forEach {(_, moduleNode) ->
                conjunction.addInputModuleNode(moduleNode)
            }
        }


        return lookupByName
    }

    fun activatePulseUsingBFS(root: ModuleNode, pulse: Pulse, buttonPresses: Int) {
        val queue = ArrayDeque<ModuleNode>()
        queue.add(root)

        var currentNode: ModuleNode?
        root.module.acceptInput(pulse, root, buttonPresses)
        var atLeastOneOutput: Boolean
        while (!queue.isEmpty()) {
//            println("queue = ${queue}")
            currentNode = queue.removeFirst()
//            println("currentNode = ${currentNode}")
            atLeastOneOutput = currentNode.module.sendToNext()

            if (atLeastOneOutput) {
                queue.addAll(currentNode.module.outputs)
            }
        }

    }

    fun part1(input: List<String>, numButtonPushes: Int): Int {

        val moduleByNameLookup = parseInput(input)
        val broadcaster = moduleByNameLookup[BROADCASTER]!!

        (1..numButtonPushes).forEach {
            activatePulseUsingBFS(broadcaster, Pulse.LOW, 0)
        }

        val totalLowPulses = moduleByNameLookup.values.sumOf {
            it.module.countLowPulses
        }
        println("totalLowPulses = ${totalLowPulses}")
        val totalHighPulses = moduleByNameLookup.values.sumOf {
            it.module.countHighPulses
        }
        println("totalHighPulses = ${totalHighPulses}")

        return totalHighPulses * totalLowPulses
    }

    fun part2(input: List<String>): Long {
        val moduleByNameLookup = parseInput(input)
        val broadcaster = moduleByNameLookup[BROADCASTER]!!
        var buttonPresses = 0

        //find out when conjunction module 'hp' input's receive a HIGH pulse

        try {
            while (true) { //or at least till an ReceivedLowPulseEvent event (masquerading as an exception)
                buttonPresses++
                if (buttonPresses % 500 == 0) {
                    println("Button presses till now = $buttonPresses")
                }
                activatePulseUsingBFS(broadcaster, Pulse.LOW, buttonPresses)
            }
        } catch (ex: ReceivedLowPulseEvent) {
            //ignore
        }

        //get the min button presses for each of 'hp' inputs and find the LCM for that
        val minPushes = (moduleByNameLookup["hp"]?.module as Conjunction).getMinButtonPushesForHighPulse()
        return findLCMOfListOfNumbers(minPushes.map { it.value.toLong() })
    }

//    val testInput = readInput("Day20_test1")
//    check(part1(testInput, 1000) == 32000000)
//    val testInput2 = readInput("Day20_test2")
//    check(part1(testInput2, 1000) == 11687500)
//    val input = readInput("Day20")
//    part1(input, 1000).println()

    val input2 = readInput("Day20")
    part2(input2).println()

}

data class ModuleNode(var module: Module)

abstract class Module(val name: String, var enclosingModuleNode: ModuleNode? = null) {
    var outputs: List<ModuleNode> = mutableListOf()
    var countLowPulses = 0
    var countHighPulses = 0
    var buttonPushesTillNow = 0
    open fun acceptInput(pulse: Pulse, originator: ModuleNode?, buttonPushesTillNow: Int) {
        if (pulse == Pulse.HIGH) {
            countHighPulses++
        } else if (pulse == Pulse.LOW) {
            countLowPulses++
        }
        this.buttonPushesTillNow = buttonPushesTillNow
        println("outputs = ${outputs}")
    }
    abstract fun sendToNext(): Boolean
}

enum class Pulse {
    LOW, HIGH
}

class Flipflop(name: String, enclosingModuleNode: ModuleNode? = null) : Module(name, enclosingModuleNode) {

    private var inputPulse: Pulse? = null
    private var switchStateOn: Boolean = false
    private val outputStore = mutableMapOf<ModuleNode, Pulse>()

    override fun acceptInput(pulse: Pulse, originator: ModuleNode?, buttonPushesTillNow: Int) {
        //println("Flipflop ${name}: Received pulse = ${pulse}, originator = $originator")
        super.acceptInput(pulse, originator, buttonPushesTillNow)

        this.inputPulse = pulse

        processAndStore();
    }

    private fun processAndStore() {
        if (this.inputPulse == Pulse.LOW) {
            var outputPulse: Pulse?
            if (switchStateOn) {
                switchStateOn = false
                outputPulse = Pulse.LOW
            } else {
                switchStateOn = true
                outputPulse = Pulse.HIGH
            }
            this.outputs.forEach { output ->
                outputStore[output] = outputPulse
            }
        }

    }

    override fun sendToNext():Boolean {
        //println("Flipflop ${name}: send to next => $outputStore")
        //ignore the HIGH pulse

        this.outputs.forEach { output ->
            if (outputStore[output] != null) {
                output.module.acceptInput(outputStore[output]!!, enclosingModuleNode, buttonPushesTillNow)
            }
        }

        val atLeastOneOutput = outputStore.isNotEmpty()
        outputStore.clear()
        return atLeastOneOutput
    }

    override fun toString(): String {
        return "Flipflop(name=$name, switchStateOn=$switchStateOn)"
    }


}

class Broadcast(name: String, enclosingModuleNode: ModuleNode? = null) : Module(name, enclosingModuleNode) {
    var input: Pulse? = null

    private val outputStore = mutableMapOf<ModuleNode, Pulse>()
    override fun acceptInput(pulse: Pulse, originator: ModuleNode?, buttonPushesTillNow: Int) {
        //println("Broadcast received pulse = ${pulse}, originator = ${originator}")
        super.acceptInput(pulse, originator, buttonPushesTillNow)
        this.input = pulse
        processAndStore()
    }

    private fun processAndStore() {
        outputs.forEach { output ->
            //broadcast just sends the same pulse again
            val thisInput = this.input
            if (thisInput != null) {
                outputStore[output] = thisInput
            }
        }


    }

    override fun sendToNext(): Boolean {
        //println("Broadcast send to next  = ${outputStore}")
        outputs.forEach { output ->
            if (outputStore[output] != null) {
                output.module.acceptInput(outputStore[output]!!, enclosingModuleNode, buttonPushesTillNow)
            }
        }
        val atLeastOneOutput = outputStore.isNotEmpty()
        outputStore.clear()
        return atLeastOneOutput
    }
    override fun toString(): String {
        return "Broadcast(name=$name)"
    }

}

class Conjunction(name: String, enclosingModuleNode: ModuleNode? = null) : Module(name, enclosingModuleNode) {

    private val inputStore = mutableMapOf<ModuleNode, Pulse>()
    private val outputStore = mutableMapOf<ModuleNode, Pulse>()

    //for part 2
    private val buttonPushesForHighPulse = mutableMapOf<ModuleNode, Int>()
    override fun acceptInput(pulse: Pulse, originator: ModuleNode?, buttonPushesTillNow: Int) {
        //println("Conjunction ${name}: Received pulse = ${pulse} and originator = ${originator}")
        super.acceptInput(pulse, originator, buttonPushesTillNow)

        if (originator != null) {
            inputStore[originator] = pulse
        }

        if (name == "hp") {
            if (pulse == Pulse.HIGH) {
                if (originator != null) {
                    buttonPushesForHighPulse[originator] = buttonPushesTillNow
                }
            }
            if (buttonPushesForHighPulse.keys.size == inputStore.keys.size) {
                println("buttonPushesForHighPulse = ${buttonPushesForHighPulse}")
                throw ReceivedLowPulseEvent("Received all high pulses !! ${buttonPushesForHighPulse}")
            }
        }
    }

    fun getMinButtonPushesForHighPulse(): MutableMap<ModuleNode, Int> {
        return buttonPushesForHighPulse
    }

    fun addInputModuleNode(moduleNode: ModuleNode) {
        inputStore[moduleNode] = Pulse.LOW //default LOW pulse
    }

    private fun processAndStoreOutput() {
        if (inputStore.values.all { it == Pulse.HIGH }) {
            outputs.forEach { output ->
                outputStore[output] = Pulse.LOW
            }
        } else {
            outputs.forEach { output ->
                outputStore[output] = Pulse.HIGH
            }
        }
        //println("Conjunction ${name} . outputStore = ${outputStore}")

    }

    override fun sendToNext(): Boolean {
        processAndStoreOutput()
        //println("Conjunction ${name} send to next  = ${outputStore}")

        outputs.forEach { output ->
            if (outputStore[output] != null) {
                output.module.acceptInput(outputStore[output]!!, enclosingModuleNode, buttonPushesTillNow)
            }
        }
        val atLeastOneOutput = outputStore.isNotEmpty()
        outputStore.clear()
        return atLeastOneOutput
    }

    override fun toString(): String {
        return "Conjunction(name=$name)"
    }

}

class Placeholder(name: String, enclosingModuleNode: ModuleNode? = null) : Module(name, enclosingModuleNode) {
    override fun acceptInput(pulse: Pulse, originator: ModuleNode?, buttonPushesTillNow: Int) {
        super.acceptInput(pulse, originator, buttonPushesTillNow)

        //for part 2
        if (name == "rx") {
            if (pulse == Pulse.LOW) {
                throw ReceivedLowPulseEvent("")
            }
        }
    }

    override fun sendToNext(): Boolean {
        //do nothing
        return false
        //throw UnsupportedOperationException("Not supposed to have a placeholder at the time of pressing the button")

    }
}

class ReceivedLowPulseEvent(s: String) : Throwable() {

}
