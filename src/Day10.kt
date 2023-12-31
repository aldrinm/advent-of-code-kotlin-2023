import java.util.*
import kotlin.math.absoluteValue

fun main() {

    //@formatter:off
    val neighboursOffsets = listOf(
                         Pair(0, -1),
            Pair(-1,  0)             , Pair(1,  0),
                         Pair(0,  1),
    )
    //@formatter:on

    fun validNeighbours(grid: Array<Array<Char>>, cell: Cell) = neighboursOffsets
            .map { offset ->
                val newX = cell.x + offset.first
                val newY = cell.y + offset.second
                Cell(newX, newY)
            }
            .filter {
                it.x >= 0
                        && it.x <= grid.get(0).size
                        && it.y >= 0
                        && it.y <= grid.size
            }

    //Determines connected neighbours (pipes) to startCell. Empty return means nothing is connected
    fun getValidConnector(neighbour: Cell, startCell: Cell, grid: Array<Array<Char>>): Pair<Char, Direction>? {
        //check the type of pipe at neighbour and determine if it can connect to startCell
        val neighbourPipe = grid[neighbour.x][neighbour.y]

        if ((startCell.x - neighbour.x) == 0 && (startCell.y - neighbour.y) == 1) {
            //neighbour at North
            //must have a pipe towards the south
            if (listOf('|', '7', 'f').contains(neighbourPipe)) {
                //yes! a connection
//                println("neighbourPipe = ${neighbourPipe} is connected")
                return Pair(neighbourPipe, Direction.N)
            }
        }

        if ((startCell.x - neighbour.x) == 0 && (startCell.y - neighbour.y) == -1)  {
            //neighbour at South
            //must have a pipe towards the north
            if (listOf('|', 'L', 'J').contains(neighbourPipe)) {
                //yes! a connection
//                println("neighbourPipe = ${neighbourPipe} is connected")
                return Pair(neighbourPipe, Direction.S)

            }
        }

        if ((startCell.x - neighbour.x) == 1 && (startCell.y - neighbour.y) == 0)  {
            //neighbour at West
            //must have a pipe towards the east
            if (listOf('-', 'L', 'F').contains(neighbourPipe)) {
                //yes! a connection
//                println("neighbourPipe = ${neighbourPipe} is connected")
                return Pair(neighbourPipe, Direction.W)

            }
        }

        if ((startCell.x - neighbour.x) == -1 && (startCell.y - neighbour.y) == 0)  {
            //neighbour at East
            //must have a pipe towards the west
            if (listOf('-', 'J', '7').contains(neighbourPipe)) {
                //yes! a connection
//                println("neighbourPipe = ${neighbourPipe} is connected")
                return Pair(neighbourPipe, Direction.E)

            }
        }

        return null
    }

    fun findNeighboursOfStartCell(startCell: Cell, grid: Array<Array<Char>>): List<Cell> {
        val validNeighbours = validNeighbours(grid, startCell)
        val legitPipeConnections = validNeighbours
                .mapNotNull {
                    println("for cell ${it} valid connectors are = ${getValidConnector(it, startCell, grid)}")
                    if (getValidConnector(it, startCell, grid) != null) it else null
                }

        if (legitPipeConnections.size != 2) {
            throw RuntimeException("Expected exactly TWO, but got ${legitPipeConnections}")
        }

        return legitPipeConnections
    }

    fun findNeighbours(cell: Cell, grid: Array<Array<Char>>): List<Cell> {
        val pipe = grid[cell.x][cell.y]
        if (pipe == 'S') {
            throw RuntimeException("Use findNeighboursOfStartCell for this cell ${cell}")
        }

        val validNeighbours = validNeighbours(grid, cell)

        return when (pipe) {
            '|' -> listOf(Cell(cell.x, cell.y - 1), Cell(cell.x, cell.y+1))
                    .filter { validNeighbours.contains(it) }
            '-' -> listOf(Cell(cell.x - 1 , cell.y), Cell(cell.x + 1, cell.y))
                    .filter { validNeighbours.contains(it) }
            'L' -> listOf(Cell(cell.x, cell.y - 1), Cell(cell.x + 1, cell.y))
                    .filter { validNeighbours.contains(it) }
            'J' -> listOf(Cell(cell.x, cell.y - 1), Cell(cell.x - 1, cell.y))
                    .filter { validNeighbours.contains(it) }
            '7' -> listOf(Cell(cell.x - 1 , cell.y), Cell(cell.x, cell.y + 1))
                    .filter { validNeighbours.contains(it) }
            'F' -> listOf(Cell(cell.x + 1 , cell.y), Cell(cell.x, cell.y + 1))
                    .filter { validNeighbours.contains(it) }
            else -> listOf()
        }
    }

    fun findNeighbours(grid: Array<Array<Char>>, nextCell: Cell, startCell: Cell): List<Cell> {
        if (nextCell.x == startCell.x && nextCell.y == startCell.y) {
            return findNeighboursOfStartCell(startCell, grid)
        } else {
            return findNeighbours(nextCell, grid)
        }
    }

    fun bfs(grid: Array<Array<Char>>, startCell: Cell): List<Cell> {
        val queue: Queue<Cell> = LinkedList()
        val visited = mutableListOf<Cell>()
        startCell.depthLevel = 0
        queue.add(startCell)
//        visited.add(startCell)
        while (!queue.isEmpty()) {
            val nextCell = queue.poll()!!
            val validNeighbours = findNeighbours(grid, nextCell, startCell)
            //println("validNeighbours = ${validNeighbours}")

            visited.add(nextCell)
            validNeighbours.forEach { neighbour ->
                if (!visited.contains(neighbour)) {
                    neighbour.depthLevel = nextCell.depthLevel + 1
                    //println("Visiting ${neighbour} ")
                    queue.add(neighbour)
                }
            }

        }

        println("visited = ${visited}")
        return visited
    }

    fun parseInput(input: List<String>): Field {
        val w = input[0].length
        val h = input.size
        val grid: Array<Array<Char>> = Array(w) {Array(h) { '.' }}
        var startCell: Cell = Cell(0,0)
        //load into grid
        input.forEachIndexed { j, s ->
            s.forEachIndexed { i, c ->
                grid[i][j] = c
                if (c == 'S') {
                    startCell = Cell(i, j)
                }
            }
        }

//        grid[startCell.x][startCell.y] = figureActualPipe(startCell, grid)

        return Field(grid, startCell)

    }



    fun part1(input: List<String>): Int {
        val field = parseInput(input)
        val visitedNodes = bfs(field.grid, field.startCell)
        return visitedNodes.maxOf { it.depthLevel }
    }

    fun getTheTravelPath(visitedNodes: List<Cell>, grid: Array<Array<Char>>): List<Cell> {
        //using depth, traverse in both directions and then join
        val dir1 = mutableListOf<Cell>()
        val dir2 = mutableListOf<Cell>()
        val start = visitedNodes.minBy { it.depthLevel }
        val end = visitedNodes.maxBy { it.depthLevel }

        //find nodes at depth 1 to start with
        val nodesAtDepthOne = visitedNodes.filter { it.depthLevel == 1 }
        dir1.add(start)
        dir1.add(nodesAtDepthOne[0])
        var dir1CurrNode = nodesAtDepthOne[0]
        dir2.add(nodesAtDepthOne[1])
        var dir2CurrNode = nodesAtDepthOne[1]

        (2..<end.depthLevel).forEach { depth ->
            val nodesAtDepth = visitedNodes.filter { it.depthLevel == depth }
                .forEach {
                    if (validNeighbours(grid, dir1CurrNode).contains(it)) {
                        dir1.add(it)
                        dir1CurrNode = it
                    } else {
                        dir2.add(it)
                        dir2CurrNode = it
                    }
                }
        }

//        println("dir1 = ${dir1}")
//        println("dir2 = ${dir2}")

        dir1.add(end)
        dir1.addAll(dir2.reversed())
        return dir1
    }

    fun calculateInternalPoints(visitedNodes: List<Cell>, grid: Array<Array<Char>>): Int {
        //ref: https://en.wikipedia.org/wiki/Shoelace_formula
        val travelPath = getTheTravelPath(visitedNodes, grid)
        val perimeterNodes = travelPath.distinct()
        val perimeter = perimeterNodes.size
        println("perimeter = ${perimeterNodes.size}")
        val area = perimeterNodes.zip(perimeterNodes.drop(1) + perimeterNodes.take(1)) { a, b -> a.x * b.y - a.y * b.x }.sum().absoluteValue / 2
        println("areas = ${area}")
        val points = area - (perimeter / 2) + 1
        println("points = ${points}")
        return points
    }

    fun part2(input: List<String>): Int {
        val field = parseInput(input)
        val visitedNodes = bfs(field.grid, field.startCell).distinct()

        //ray-cast from each cell not in the border in the right direction
        println("visitedNodes = ${visitedNodes}")

        return calculateInternalPoints(visitedNodes, field.grid)
    }

//    val testInput1 = readInput("Day10_test1")
//    check(part1(testInput1) == 4)
//    val testInput2 = readInput("Day10_test2")
//    check(part1(testInput2) == 8)
//    val input = readInput("Day10")
//    part1(input).println()

//    val testInput2 = readInput("Day10_test21")
//    check(part2(testInput2) == 4)
    val input2 = readInput("Day10")
    part2(input2).println()

}

data class Field(val grid: Array<Array<Char>>, val startCell: Cell)

data class Cell(val x: Int, val y: Int, var depthLevel: Int = -1) {
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

enum class Direction {
    N, S, W, E
}