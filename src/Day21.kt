fun main() {

    fun parseInput(input: List<String>): Garden {
        return Garden(input)
    }

    fun gardenFloodfill(garden: Garden, startPoint: Pair<Int, Int>, steps: Int) {
        println("starting flood fill...")
        val queue = ArrayDeque<Pair<Int, Int>>()
        queue.add(startPoint)

        var currentPos: Pair<Int, Int>?
        while (!queue.isEmpty()) {
//            println("queue = ${queue}")
            currentPos = queue.removeFirst()
//            println("currentNode = ${currentNode}")
            val currentPlot = garden.get(currentPos.first, currentPos.second)
            val neighbours = garden.neighbours(currentPos)

            if (currentPlot.stepNumber < steps) {
//                queue.addAll(neighbours)
                queue.addAll(neighbours.filter { garden.get(it.first, it.second).stepNumber <= currentPlot.stepNumber })
                neighbours.forEach {
                    garden.get(it.first, it.second).stepNumber = currentPlot.stepNumber + 1
                }
            }
        }

    }

    fun part1(input: List<String>, steps: Int): Int {
        val garden = parseInput(input)

        gardenFloodfill(garden, garden.startPoint, steps)

        garden.printGrid()

        return garden.countPlotsWithStepNumber(steps)
    }

    fun part2(input: List<String>): Int {
        return 1
    }

//    val testInput = readInput("Day21_test")
//    check(part1(testInput, 6) == 16)
    val input = readInput("Day21")
    part1(input, 64).println()
//
//    val testInput2 = readInput("Day21_test")
//    check(part2(testInput2) == 281)
//    val input2 = readInput("Day21")
//    part2(input2).println()

}

class Garden(val input: List<String>) {

    var grid: Array<Array<Plot>> = Array(input.size) {Array(input[0].length) { Plot(-1, -1, ' ')}}
    var startPoint: Pair<Int, Int> = Pair(0, 0)

    private val neighbourOffsets = listOf(Pair(0, -1), Pair(1, 0), Pair(0, 1), Pair(-1, 0))

    init {
        //load into grid
        input.forEachIndexed { j, s ->
            s.forEachIndexed { i, c ->
                if (c == 'S') {
                    grid[j][i] = Plot(i, j, '.')
                    startPoint = Pair(i, j)
                } else {
                    grid[j][i] = Plot(i, j, c)
                }
            }
        }
    }

    fun width(): Int {
        return grid[0].size
    }

    fun height(): Int {
        return grid.size
    }

    fun get(x: Int, y: Int): Plot {
        return grid[y][x]
    }


    fun printGrid() {
        println("------------------------")
        grid.forEach { row ->
            row.forEach {
                if (it.c == '.') {
                    print(it.stepNumber)
                } else {
                    print(it.c)
                }
            }
            println("")
        }
        println("------------------------")
    }

    fun neighbours(current: Pair<Int, Int>): Collection<Pair<Int, Int>> {
        return neighbourOffsets.map {
            Pair(current.first + it.first, current.second + it.second)
        }.filter {
            it.first >= 0 && it.first < width()
                    && it.second >= 0 && it.second < height()
        }.filter {
            get(it.first, it.second).c == '.'
        }
    }

    fun countPlotsWithStepNumber(steps: Int): Int {
        return (0..width()-1).sumOf { i ->
            (0..height()-1).filter { j ->
                get(i, j).stepNumber == steps
            }.count()
        }
    }

}

data class Plot(val x: Int, val y: Int, val c: Char, var stepNumber: Int = 0)
