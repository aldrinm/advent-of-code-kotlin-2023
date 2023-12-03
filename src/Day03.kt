fun main() {

    val neighboursOffsets = listOf(
        Pair(-1, -1), Pair(0, -1), Pair(1, -1),
        Pair(-1,  0)             , Pair(1,  0),
        Pair(-1,  1), Pair(0,  1), Pair(1,  1)
    )



    fun readGrid(input: List<String>): Array<Array<Char>> {
        val w = input[0].length
        val h = input.size
        val grid: Array<Array<Char>> = Array(w) {Array(h) { '.' }}

        //load into grid
        input.forEachIndexed { j, s ->
            s.forEachIndexed { i, c ->
                grid[i][j] = c
            }
        }
        return grid
    }

    fun collectRowString(grid: Array<Array<Char>>, rowNumber: Int): String {
        return (0..<grid[0].size)
            .map { grid[it][rowNumber] }
            .joinToString("")
    }

    fun findPositionFirstDigitOnLeft(grid: Array<Array<Char>>, touchesPosition: Pair<Int, Int>): Int {
        val row = collectRowString(grid, touchesPosition.second)
        var currX = touchesPosition.first
        while (currX >= 1 && row[currX-1].isDigit()) {
            --currX
        }
        return currX
    }

    fun extractNumberAndStartPosition(grid: Array<Array<Char>>, touchesPosition: Pair<Int, Int>): NumberAndPosition {
        val leftmostPos = findPositionFirstDigitOnLeft(grid, touchesPosition)
        val patternNumber = "(\\d+)".toRegex()
        val (n) = patternNumber.find(collectRowString(grid, touchesPosition.second), leftmostPos)!!.destructured
//        println("n = ${n}")

        return NumberAndPosition(n.toInt(), Pair(leftmostPos, touchesPosition.second))

    }

    fun findNeighbourNumbers(
        c: Char,
        neighboursOffsets: List<Pair<Int, Int>>,
        i: Int,
        j: Int,
        grid: Array<Array<Char>>
    ): Set<NumberAndPosition> {
        val numbersNearSymbols = mutableSetOf<NumberAndPosition>()
        if (!c.isDigit() && c != '.') {
    //                    println("Found a symbol $c at $i, $j")
            val validNeighbours = neighboursOffsets
                .map { offset ->
                    val newX = i + offset.first
                    val newY = j + offset.second
                    Pair(newX, newY)
                }
                .filter {
                    it.first >= 0
                            && it.first <= grid.get(0).size
                            && it.second >= 0
                            && it.second <= grid.size
                }

            //check if we have a digit in the validNeighbours
            validNeighbours
                .map { pos ->
                    if (grid[pos.first][pos.second].isDigit()) {
    //                                println("we got a digit ${grid[pos.first][pos.second]} at ${pos.first}, ${pos.second}")
                        val numberAndPosition = extractNumberAndStartPosition(grid, pos)
    //                                println("Matched number = ${numberAndPosition}")
                        numbersNearSymbols.add(numberAndPosition)
                    }
                }
        }
        return numbersNearSymbols
    }

    fun findNumbersNearSymbolsPart1(grid: Array<Array<Char>>): List<Int> {
        //find neighbours
        val numbersNearSymbols = mutableSetOf<NumberAndPosition>()

        for (j in 0..<grid.size) {
            for (i in 0..<grid[j].size) {

//                print(grid[i][j])
                val c = grid[i][j]
                numbersNearSymbols.addAll(findNeighbourNumbers(c, neighboursOffsets, i, j, grid))

            }
        }

        return numbersNearSymbols.map { it.number }
    }

    fun part1(input: List<String>): Int {
        val grid = readGrid(input)
        val numbersNearSymbols = findNumbersNearSymbolsPart1(grid)
//        println("findNumbersNearSymbols(grid) = $numbersNearSymbols")
        return numbersNearSymbols.sum()
    }

    fun findNumbersNearGearsPart2(grid: Array<Array<Char>>): List<Int> {
        val gearRatio = mutableListOf<Int>()

        for (j in 0..<grid.size) {
            for (i in 0..<grid[j].size) {

//                print(grid[i][j])
                val c = grid[i][j]
                if (c == '*') {
                    //gears
                    val gearNumbers = findNeighbourNumbers(c, neighboursOffsets, i, j, grid)
//                    println("gearNumbers = ${gearNumbers}")
                    if (gearNumbers.size == 2) {
                        gearRatio.add(gearNumbers.map{it.number}.reduce { acc, n ->  acc * n})
                    }
                }

            }
        }

        return gearRatio
    }

    fun part2(input: List<String>): Int {
        val grid = readGrid(input)
        return findNumbersNearGearsPart2(grid).sum()
    }

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    val input = readInput("Day03")
    part1(input).println()

    val testInput2 = readInput("Day03_test")
    check(part2(testInput2) == 467835)
    val input2 = readInput("Day03")
    part2(input2).println()
}

data class NumberAndPosition(val number: Int, val position: Pair<Int, Int>)
