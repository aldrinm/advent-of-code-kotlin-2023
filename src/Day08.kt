fun main() {

    fun findLCM(a: Long, b: Long): Long {
        val larger = if (a > b) a else b
        val maxLcm = a * b
        var lcm = larger
        while (lcm <= maxLcm) {
            if (lcm % a == 0L && lcm % b == 0L) {
                return lcm
            }
            lcm += larger
        }
        return maxLcm
    }

    fun findLCMOfListOfNumbers(numbers: List<Long>): Long {
        var result = numbers[0]
        for (i in 1 until numbers.size) {
            result = findLCM(result, numbers[i])
        }
        return result
    }


    fun parseInput(input: List<String>): NodeMap {
        val moves = input[0]
        val pattern = "(.*) = \\((.*), (.*)\\)".toRegex()
        val nodes= input.subList(2, input.size)
            .filter { it.isNotEmpty() }
            .map {
                val (curr, left, right) = pattern.find(it)?.destructured!!
                Node(curr, left, right)
            }
            .associateBy { it.curr }
        return NodeMap(moves, nodes)
    }

    fun incrementMove(moveIndex: Int, moves: String): Int = (moveIndex + 1) % moves.length

/*
    //Earlier implementation
    fun incrementMove(moveIndex: Int, moves: String): Int = if ((moveIndex + 1) >= moves.length) {
        0
    } else {
        moveIndex + 1
    }

*/
    fun part1(input: List<String>): Int {
        val (moves, nodes) = parseInput(input)
        val startNode = "AAA"
        val targetNode = "ZZZ"

        var currNode = nodes[startNode]!!
        var moveIndex = 0
        var steps = 0
        while (currNode.curr != targetNode) {
            when (moves[moveIndex]) {
                'L' -> currNode = nodes[currNode.left]!!
                'R' -> currNode = nodes[currNode.right]!!
            }
            moveIndex = incrementMove(moveIndex, moves)
            steps++
        }

        return steps
    }

    fun part2(input: List<String>): Long {
        val (moves, nodes) = parseInput(input)
        val startNodes = nodes.keys.filter { it.endsWith("A") }

        val steps = startNodes.map {startNode ->
            var currNode = nodes[startNode]!!
            var moveIndex = 0
            var steps = 0
            while (!currNode.curr.endsWith("Z")) {
                when (moves[moveIndex]) {
                    'L' -> currNode = nodes[currNode.left]!!
                    'R' -> currNode = nodes[currNode.right]!!
                }
                moveIndex = incrementMove(moveIndex, moves)
                steps++
            }
            steps.toLong()
        }

        return findLCMOfListOfNumbers(steps)
    }

    val testInput11 = readInput("Day08_test1")
    check(part1(testInput11) == 2)
    val testInput12 = readInput("Day08_test2")
    check(part1(testInput12) == 6)
    val input = readInput("Day08")
    part1(input).println()

    println("Test #1")
    val testInput21 = readInput("Day08_test3")
    check(part2(testInput21) == 6L)
    println("Test #2")
    val testInput22 = readInput("Day08_test1")
    check(part2(testInput22) == 2L)
    println("Test #3")
    val testInput23 = readInput("Day08_test2")
    check(part2(testInput23) == 6L)


    val input2 = readInput("Day08")
    part2(input2).println()

}

data class NodeMap(val moves: String, val nodes: Map<String, Node>)
data class Node(val curr: String, val left: String, val right: String)
