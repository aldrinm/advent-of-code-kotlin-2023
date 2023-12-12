import kotlin.math.absoluteValue

fun main() {

    fun printGrid(grid: Array<Array<Char>>) {
        (0..<grid[0].size).forEach { j ->
            (0..<grid.size).forEach { i ->
                print(grid[i][j])
            }
            println()
        }
    }

    fun shortestPath(g1: Sector, g2: Sector): Int {
        return (g1.x - g2.x).absoluteValue + (g1.y - g2.y).absoluteValue
    }

    fun parseAndCalculateGalaxies(input: List<String>): Int {
        val newInput = mutableListOf<String>()
        input.forEachIndexed { index, s ->
            if (s.toCharArray().all { it == '.' }) {
                //add twice !
                newInput.add(s)
                newInput.add(s)
            } else {
                newInput.add(s)
            }
        }

        val colIndexesEmpty = (0..<newInput[0].length).filter { colInd ->
            newInput.all { row ->
                row[colInd] == '.'
            }
        }
        val newerInput = newInput.map {
            val tempStr = StringBuilder(it)
            colIndexesEmpty.reversed().forEachIndexed { index, colInd ->
                tempStr.insert(colInd, '.')
            }
            tempStr.toString()
        }

        val w = newerInput[0].length
        val h = newerInput.size
        val grid: Array<Array<Char>> = Array(h) {Array(w) { '.' }}
        val galaxies = mutableListOf<Sector>()
        newerInput.forEachIndexed { j, s ->
            s.forEachIndexed { i, c ->
                grid[j][i] = c
                if (c == '#') {
                    galaxies.add(Sector(j,i))
                }
            }
        }
        printGrid(grid)

        println("galaxies = ${galaxies}")

        //combine
        var sumOfShortestPaths = 0
        for (i in 0..<galaxies.size) {
            val g1 = galaxies[i]
            for (j in (i + 1)..<galaxies.size) {
                val g2= galaxies[j]
                sumOfShortestPaths += shortestPath(g1, g2)
            }
        }

        return sumOfShortestPaths

    }

    fun part1(input: List<String>): Int {
        return parseAndCalculateGalaxies(input)
    }

    fun parseInput(input: List<String>): Pair<Array<Array<Sector>>, MutableList<Sector>> {
        val universe = Array(input.size) { Array(input[0].length) { Sector(-1, -1) } }
        val galaxies = mutableListOf<Sector>()
        input.forEachIndexed { j, s ->
            s.forEachIndexed { i, c ->
                val sector = Sector(i, j)
                if (c == '#') {
                    sector.hasGalaxy = true
                    galaxies.add(sector)
                }
                universe[j][i] = sector
            }
        }
        return Pair(universe, galaxies)
    }

    fun shortestPathPart2(universe: Array<Array<Sector>>, g1: Sector, g2: Sector): Long {
        var pathDistance = 0L

        val fromG = listOf(g1, g2).minByOrNull { it.x }!!
        val toG = listOf(g1, g2).maxByOrNull { it.x }!!

        ((fromG.x+1)..toG.x).forEach {
            pathDistance += universe[fromG.y][it].multiplierFactorX
        }

        val fromY = minOf(g1.y, g2.y)
        val toY = maxOf(g1.y, g2.y)
        ((fromY+1)..toY).forEach {
            pathDistance += universe[it][toG.x].multiplierFactorY
        }

        return pathDistance
    }

    fun part2(input: List<String>, multiplierFactor: Int): Long {
        val (universe, galaxies) = parseInput(input)

        //check which rows are all empty, and set their multiplier factor accordingly
        universe.forEach { g ->
            if (g.all { !it.hasGalaxy }) {
                g.forEach { it.multiplierFactorY = multiplierFactor }
            }
        }

        //now same for column
        val colInds = (0..<universe[0].size).filter { i ->
            (0..<universe.size).all { j ->
                !universe[j][i].hasGalaxy
            }
        }
        //println("colInds = ${colInds}")
        colInds.forEach { i ->
            (0..<universe.size).forEach { j ->
                universe[j][i].multiplierFactorX = multiplierFactor
            }
        }

        var sumOfShortestPaths = 0L
        for (i in 0..<galaxies.size) {
            val g1 = galaxies[i]
            for (j in (i + 1)..<galaxies.size) {
                val g2= galaxies[j]
                sumOfShortestPaths += shortestPathPart2(universe, g1, g2)
            }
        }

        println("sumOfShortestPaths = ${sumOfShortestPaths}")
        return sumOfShortestPaths


    }

//    val testInput = readInput("Day11_test")
//    check(part1(testInput) == 374)
//    val input = readInput("Day11")
//    part1(input).println()

    val testInput2 = readInput("Day11_test")
    check(part2(testInput2, 2) == 374L)
    check(part2(testInput2, 10) == 1030L)
    check(part2(testInput2, 100) == 8410L)
    val input2 = readInput("Day11")
    part2(input2, 1_000_000).println()

}

data class Sector(val x: Int, val y: Int, var hasGalaxy: Boolean = false, var multiplierFactorX: Int = 1,
                  var multiplierFactorY: Int = 1) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Cell

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
}
