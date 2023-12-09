fun main() {

    fun parseInput(input: List<String>): List<List<Int>> {
        return input.map {
            it.split(" ")
                .map { it.trim().toInt() }
        }

    }

    fun part1(input: List<String>): Int {
        val histories = parseInput(input)

        return histories.sumOf { history ->
            var currDiffs = history
            var lastValues = mutableListOf<Int>()
            while (currDiffs.distinct().count() != 1) {
                //it is enough that we find where all are the same value
                lastValues.add(currDiffs.last())
                //it is enough that we find where all are the same value
                currDiffs = currDiffs
                    .zipWithNext()
                    .map { it.second - it.first }
            }
            lastValues.add(currDiffs.last())

            lastValues.reversed().reduce {acc, i ->
                acc + i
            }
        }
    }

    fun part2(input: List<String>): Int {
        val histories = parseInput(input)

        return histories.sumOf { history ->
            var currDiffs = history
            var firstValues = mutableListOf<Int>()
            while (currDiffs.distinct().count() != 1) {
                //it is enough that we find where all are the same value
                firstValues.add(currDiffs.first())
                //it is enough that we find where all are the same value
                currDiffs = currDiffs
                    .zipWithNext()
                    .map { it.second - it.first }
            }
            firstValues.add(currDiffs.first())

            firstValues.reversed().reduce { acc, i ->
                i - acc
            }.also (::println)
        }
    }

//    val testInput = readInput("Day09_test")
//    check(part1(testInput) == 114)
//    val input = readInput("Day09")
//    part1(input).println()

    val testInput2 = readInput("Day09_test")
    check(part2(testInput2) == 2)
    val input2 = readInput("Day09")
    part2(input2).println()

}