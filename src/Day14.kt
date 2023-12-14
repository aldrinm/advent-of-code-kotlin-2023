fun main() {

    fun moveUp(dish: Dish, i: Int, j: Int) {
        val openSpaces = (0..(j - 1)).reversed().takeWhile {
            dish.get(i, it) == '.'
        }
        //println("takeWhile = ${takeWhile}")

        if (openSpaces.isNotEmpty()) {
            dish.moveRock(i, j, i, openSpaces.min())
        }
    }

    fun moveDown(dish: Dish, i: Int, j: Int) {
        val openSpaces = ((j+1)..dish.height()-1).takeWhile {
            dish.get(i, it) == '.'
        }
        //println("takeWhile = ${takeWhile}")

        if (openSpaces.isNotEmpty()) {
            dish.moveRock(i, j, i, openSpaces.max())
        }
    }

    fun moveLeft(dish: Dish, i: Int, j: Int) {
        val openSpaces = (0..(i - 1)).reversed().takeWhile {
            dish.get(it, j) == '.'
        }
        if (openSpaces.isNotEmpty()) {
            dish.moveRock(i, j, openSpaces.min(), j)
        }
    }

    fun moveRight(dish: Dish, i: Int, j: Int) {
        val openSpaces = ((i+1)..(dish.width()-1)).takeWhile {
            dish.get(it, j) == '.'
        }
        if (openSpaces.isNotEmpty()) {
            dish.moveRock(i, j, openSpaces.max(), j)
        }
    }

    fun tiltNorth(dish: Dish) {
        (0..dish.width() - 1).forEach { i ->
            (0..dish.height() - 1).forEach { j ->
                if (dish.get(i, j) == 'O') {
                    //for each rock move it as up as possible
                    moveUp(dish, i, j)
                }
            }
        }
    }

    fun part1(input: List<String>): Int {
        val dish = Dish(input)

        //tilt north
        tiltNorth(dish)

        return dish.calculateLoad()
    }

    fun tiltWest(dish: Dish) {
        (0..dish.height() - 1).forEach { j ->
            (0..dish.width() - 1).forEach { i ->
                if (dish.get(i, j) == 'O') {
                    moveLeft(dish, i, j)
                }
            }
        }
    }

    fun tiltSouth(dish: Dish) {
        (0..dish.width() - 1).forEach { i ->
            (0..dish.height() - 1).reversed().forEach { j ->
                if (dish.get(i, j) == 'O') {
                    moveDown(dish, i, j)
                }
            }
        }
    }

    fun tiltEast(dish: Dish) {
        (0..dish.height() - 1).forEach { j ->
            (0..dish.width() - 1).reversed().forEach { i ->
                if (dish.get(i, j) == 'O') {
                    moveRight(dish, i, j)
                }
            }
        }
    }

    fun part2(input: List<String>): Int {
        val dish = Dish(input)

        val gridHashCodes = mutableSetOf<Int>()


        //TODO: incomplete ... takes too long to brute force, have to detect if there is a cycle
        (1..1_000).forEach {
            tiltNorth(dish)
//        dish.printGrid()

            tiltWest(dish)
//        dish.printGrid()

            tiltSouth(dish)
//        dish.printGrid()

            tiltEast(dish)
//        dish.printGrid()
            if (it%10_000 == 0) {
                println("done with ... ${it}. gridHashCodes.size=${gridHashCodes.size}")
            }

            gridHashCodes.add(dish.grid.hashCode())
//            println("After ${it} cycle...")
//            dish.printGrid()
        }

        return dish.calculateLoad()
    }

//    val testInput = readInput("Day14_test")
//    check(part1(testInput) == 136)
//    val input = readInput("Day14")
//    part1(input).println()

    val testInput2 = readInput("Day14_test")
    check(part2(testInput2) == 1000)
//    val input2 = readInput("Day14")
//    part2(input2).println()

}

class Dish(val input: List<String>) {

    var grid: Array<Array<Char>> = Array(input.size) {Array(input[0].length) { '.' }}
    init {
        //load into grid
        input.forEachIndexed { j, s ->
            s.forEachIndexed { i, c ->
                grid[j][i] = c
            }
        }
    }

    fun width(): Int {
        return grid[0].size
    }

    fun height(): Int {
        return grid.size
    }

    fun get(x: Int, y: Int): Char {
        return grid[y][x]
    }

    fun moveRock(fromX: Int, fromY: Int, toX: Int, toY: Int) {
        grid[fromY][fromX] = '.'
        grid[toY][toX] = 'O'
    }

    fun calculateLoad(): Int {
        return grid.mapIndexed { index, row ->
            val c = row.count { it == 'O' }
            c * (grid.size - index)
        }
//            .also(::println)
        .sum()
    }

    fun printGrid() {
        println("------------------------")
        grid.forEach { row ->
            row.forEach {
                print(it)
            }
            println("")
        }
        println("------------------------")
    }
}
