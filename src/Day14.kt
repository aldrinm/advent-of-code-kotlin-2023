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

    //This method is repeated in the class too, so watch out!
    fun calculateLoad(grid: Array<Array<Char>>): Int {
        return grid.mapIndexed { index, row ->
            val c = row.count { it == 'O' }
            c * (grid.size - index)
        }
            .sum()

    }


    fun part2(input: List<String>): Int {
        val dish = Dish(input)

        //hashCode of grid to Pair<grid, iteration number>
        val gridHashCodes = mutableMapOf<Int, Pair<Array<Array<Char>>, Int> >()

        var i = 1
        var repeatsEvery: Int = -1
        var firstOccurrence = -1
        while (i <= 1_000_000) {
            tiltNorth(dish)
//        dish.printGrid()

            tiltWest(dish)
//        dish.printGrid()

            tiltSouth(dish)
//        dish.printGrid()

            tiltEast(dish)
//        dish.printGrid()
            if (i%10_000 == 0) {
                println("done with ... ${i}. gridHashCodes.size=${gridHashCodes.size}")
            }

            if (gridHashCodes.get(dish.grid.contentDeepHashCode()) != null) {
                println("Repeated at $i ")
                println("gridHashCodes.get(dish.grid.contentDeepHashCode()) = ${gridHashCodes.get(dish.grid.contentDeepHashCode())}")
                firstOccurrence = gridHashCodes.get(dish.grid.contentDeepHashCode())?.second!!
                repeatsEvery = i - firstOccurrence
                break;
            }
            gridHashCodes.put(dish.grid.contentDeepHashCode(), Pair(deepCopyArray(dish.grid), i))

//            val currCount = gridHashCodes.getOrDefault(dish.grid.contentDeepHashCode(), 0)
//            gridHashCodes.put(dish.grid.contentDeepHashCode(), currCount + 1)
//            dish.printGrid()
            i++
        }

        println("-- all so far --")
        gridHashCodes.values.forEach {
            println("${it} => ${calculateLoad(it.first)}")
        }

        if (repeatsEvery > -1) {
            println("Pattern repeats every ${repeatsEvery}th time after the ${firstOccurrence} iteration")
            //therefore, 1_000_000_000 occurrence will be same as,
            val finalLayoutIterationNumber = (1_000_000_000 - firstOccurrence) % (repeatsEvery) + firstOccurrence
            val finalGrid = gridHashCodes.values.filter { it.second == finalLayoutIterationNumber }.first()
            println("finalGrid.first.calculateLoad() = ${calculateLoad(finalGrid.first)}")
            return calculateLoad(finalGrid.first)
        } else {
            throw RuntimeException("this is impossible!")
        }
    }

//    val testInput = readInput("Day14_test")
//    check(part1(testInput) == 136)
//    val input = readInput("Day14")
//    part1(input).println()

//    val testInput2 = readInput("Day14_test")
//    check(part2(testInput2) == 64)
    val input2 = readInput("Day14")
    part2(input2).println()

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
