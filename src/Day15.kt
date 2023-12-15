import java.util.*

fun main() {

    fun parseInput(input: List<String>): List<String> {
        val steps = input[0].split(",")
        return steps
    }

    fun calculateHash(step: String): Int {
        var currValue = 0
        step.toCharArray().forEach {
            currValue = ((currValue + it.code) * 17) % 256
            //println("for ${it} and ascii ${it.code} the currValue = ${currValue}")
        }
        return currValue
    }

    fun part1(input: List<String>): Int {
        val steps = parseInput(input)
        val sum = steps.sumOf { step ->
            var currValue = calculateHash(step)
            currValue
        }
        return sum
    }

    fun part2(input: List<String>): Int {
        val steps = parseInput(input)
        val boxes = mutableMapOf<Int, Box>()
        steps.forEach { step ->
            val operation: String
            var focal = ""
            val removeOrAdd: RemoveOrAdd
            if (step.contains("=")) {
                val (tmpOperation, tmpFocal) = step.split("=")
                operation = tmpOperation
                focal = tmpFocal
                removeOrAdd = RemoveOrAdd.ADD
            } else if (step.endsWith("-")) {
                operation = step.substringBefore("-")
                removeOrAdd = RemoveOrAdd.REMOVE
            } else {
                throw RuntimeException("Doesn't have a '-' nor an '=' -> ${step}")
            }
            val hash = calculateHash(operation)
            //println("operation = ${operation} and focal = ${focal} and hash = ${hash}")
            when (removeOrAdd) {
                RemoveOrAdd.ADD -> {
                    val box = boxes.getOrDefault(hash, Box(LinkedList()))
                    val lenses = box.lenses

                    val lensPresent = lenses.find { lens ->
                        lens.label == operation
                    }
                    if (lensPresent != null) {
                        lensPresent.focal = focal
                    } else {
                        lenses.add(Lens(operation, focal))
                    }

                    boxes[hash] = Box(lenses)
                }

                RemoveOrAdd.REMOVE -> {
                    val box = boxes[hash]
                    if (box != null) {
                        val lenses = box.lenses
                        lenses.removeIf {lens ->
                            lens.label == operation
                        }

                        boxes[hash] = Box(lenses)
                    }
                }
            }
        }

        return boxes.map { box ->
            val sumOfLenses = box.value.lenses.mapIndexed { index, lens ->
                (index + 1) * lens.focal!!.toInt() * (box.key + 1)
            }.sum()
            sumOfLenses
        }.sum()
    }

//    val testInput = readInput("Day15_test")
//    check(part1(testInput) == 1320)
//    val input = readInput("Day15")
//    part1(input).println()

//    val testInput2 = readInput("Day15_test")
//    check(part2(testInput2) == 145)
    val input2 = readInput("Day15")
    part2(input2).println()

}

enum class RemoveOrAdd {
    REMOVE, ADD
}

data class Box(val lenses: LinkedList<Lens>)
data class Lens(val label: String, var focal: String?)
