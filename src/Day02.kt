fun main() {

    fun parseInput(input: List<String>): List<Game> {

        val gamePattern = "(\\d+)(.+)".toRegex()
        val games = input.map {
            //find game number
            val splitOnColon = it.split(":")
            if (splitOnColon.isEmpty()) throw RuntimeException(" No colons in ${it}")
            val gameNumber = splitOnColon[0].replaceFirst("Game", "").trim().toInt()

            val splitOnSemi = splitOnColon[1].split(";")
            if (splitOnSemi.isEmpty()) throw RuntimeException(" No semi colons in ${it}")

            val rounds = splitOnSemi.map { eachRound ->
                val splitOnComma = eachRound.split(",")
                if (splitOnComma.isEmpty()) throw RuntimeException(" No commas in ${it}")

                var red = 0
                var blue = 0
                var green = 0
                splitOnComma.forEach { everyColour ->
                    val (n, colourWithSpace) = gamePattern.find(everyColour)!!.destructured
                    val colour = colourWithSpace.trim()
                    if (colour == "red") {
                        red = n.trim().toInt()
                    } else if (colour == "green") {
                        green = n.trim().toInt()
                    } else if (colour == "blue") {
                        blue = n.trim().toInt()
                    }
                }
                Draw(red, blue, green)

            }
            Game(gameNumber, rounds)
        }

        return games
    }

    fun updateMaxOfEachColour(games: List<Game>) {
        games.forEach { game ->
            var maxRed= game.rounds.maxOf { it.red }
            var maxBlue = game.rounds.maxOf { it.blue }
            var maxGreen = game.rounds.maxOf { it.green }
            game.maxRed = maxRed
            game.maxBlue = maxBlue
            game.maxGreen = maxGreen
        }
    }

    fun part1(input: List<String>, redCubes: Int, greenCubes: Int, blueCubes: Int): Int {
        val games = parseInput(input)
        updateMaxOfEachColour(games)

        val sumOf = games.sumOf { game ->
            println("\ngame = ${game}")
            if (game.maxRed <= redCubes
                && game.maxBlue <= blueCubes
                && game.maxGreen <= greenCubes
            ) {
                println("game.n = ${game.n}")
                game.n
            }
            else {
                println("game = 0")
                0
            }
        }
        println("sumOf = ${sumOf}")
        return sumOf
    }

    fun part2(input: List<String>): Int {
        val games = parseInput(input)
        updateMaxOfEachColour(games)
        val sumOf = games.sumOf { game ->
            game.maxBlue * game.maxGreen * game.maxRed
        }
        return sumOf
    }

    val testInput = readInput("Day02_part1_test")
    check(part1(testInput, 12, 13, 14) == 8)
    val input = readInput("Day02_part1")
    part1(input, 12, 13, 14).println()

    val testInput2 = readInput("Day02_part1_test")
    check(part2(testInput2) == 2286)
    val input2 = readInput("Day02_part1")
    part2(input2).println()
}

data class Game(val n: Int, val rounds: List<Draw>,
                var maxRed: Int = 0, var maxBlue: Int = 0, var maxGreen: Int = 0)
data class Draw(val red: Int, val blue: Int, val green: Int)

