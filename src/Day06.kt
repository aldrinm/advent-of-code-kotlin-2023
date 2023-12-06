fun main() {
    fun parseInput(input: List<String>): RaceDetails {
        val raceTimes = input[0].substringAfter("Time:")
            .split(" ")
            .filter { it.isNotBlank() }
            .map { it.trim().toInt() }
        val raceDistances = input[1].substringAfter("Distance:")
            .split(" ")
            .filter { it.isNotBlank() }
            .map { it.trim().toInt() }
        return RaceDetails(raceTimes, raceDistances)
    }

    fun parseInputPart2(input: List<String>): RaceDetailsPart2 {
        val raceTime = input[0].substringAfter("Time:")
            .replace(" ", "")
            .toLong()

        val raceDistance = input[1].substringAfter("Distance:")
            .replace(" ", "")
            .toLong()

        return RaceDetailsPart2(raceTime, raceDistance)
    }

    fun part1(input: List<String>): Int {
        val raceDetails = parseInput(input)

        return raceDetails.distances.mapIndexed { i, distance ->
            val time = raceDetails.times[i]
            (1..time - 1)
                .map { chargeTime ->
                    (time - chargeTime) * chargeTime
                }
                .filter { it > distance }
                .count()
        }.reduce { acc, i -> acc * i }
    }

    fun part2(input: List<String>): Int {
        val raceDetails = parseInputPart2(input)

        val time = raceDetails.time
        return (1..time - 1)
            .map { chargeTime ->
                (time - chargeTime) * chargeTime
            }
            .filter { it > raceDetails.distance }
            .count()
    }

//    val testInput = readInput("Day06_test")
//    check(part1(testInput) == 288)
//    val input = readInput("Day06")
//    part1(input).println()

    val testInput2 = readInput("Day06_test")
    check(part2(testInput2) == 71503)
    val input2 = readInput("Day06")
    part2(input2).println()
}

data class RaceDetails(val times:List<Int>, val distances: List<Int>)
data class RaceDetailsPart2(val time:Long, val distance: Long)
