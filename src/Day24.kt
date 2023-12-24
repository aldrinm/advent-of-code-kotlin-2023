data class Point(val x: Double, val y: Double) {
    operator fun plus(n2: Point): Point {
        return Point(x + n2.x, y+ n2.y)
    }

}

fun main() {

    fun parseInput(input: List<String>): List<Hailstone> {
        return input.map { str ->
            val (posStr, velStr) = str.split("@")
            val (px, py, pz) = posStr.split(",").map { it.trim().toDouble() }
            val (vx, vy, vz) = velStr.split(",").map { it.trim().toDouble() }

            Hailstone(px, py, pz, vx, vy, vz)
        }
    }

    fun getPointOfIntersection(p1: Point, p2: Point, n1: Point, n2: Point): Point? {
        val p1End = p1 + n1 // another point in line p1->n1
        val p2End = p2 + n2 // another point in line p2->n2

        val m1 = (p1End.y - p1.y) / (p1End.x - p1.x) // slope of line p1->n1
        val m2 = (p2End.y - p2.y) / (p2End.x - p2.x) // slope of line p2->n2

        val b1 = p1.y - m1 * p1.x // y-intercept of line p1->n1
        val b2 = p2.y - m2 * p2.x // y-intercept of line p2->n2

        if ((m1 - m2) != 0.toDouble()) {
            val px = (b2 - b1) / (m1 - m2) // collision x
            val py = m1 * px + b1 // collision y
            return Point(px, py) // return statement
        } else {
            return null
        }

    }

    fun part1(input: List<String>, testAreaMin: Double, testAreaMax: Double): Int {
        val hailstones = parseInput(input)
//        hailstones.forEach { println(it) }

        val intersectingHailstones = mutableSetOf<Pair<Hailstone, Hailstone>>()

        hailstones.forEachIndexed { i, a ->
            (i+1..hailstones.size-1).forEach { j ->
                //println("a = ${a} , b = ${hailstones[j]}")
                val b = hailstones[j]
                val poi = getPointOfIntersection(Point(a.px, a.py), Point(b.px, b.py),
                    Point(a.vx, a.vy), Point(b.vx, b.vy))
                //println("poi = ${poi}")
                if (poi != null) {
                    if (poi.x >= testAreaMin && poi.x <= testAreaMax
                        && poi.y >= testAreaMin && poi.y <= testAreaMax
                    ) {
                        println("poi = ${poi} for ${a}, ${b}")

                        /*
                        poi.x = a.px + n*a.vx  //poi will be some n multiple of the vx
                        if n is positive then it means it is in the future of 'a' else the opposite
                        */
                        val nOfAForX = (poi.x - a.px) / a.vx //either x or y will do
                        val nOfBForX = (poi.x - b.px) / b.vx
                        if (nOfAForX > 0 && nOfBForX > 0) {
                            intersectingHailstones.add(Pair(a, b))
                        }

//                    println("(poi.x - a.px)/a.vx = ${(poi.x - a.px)/a.vx}")
//                    println("(poi.y - a.py)/a.vy = ${(poi.y - a.py)/a.vy}")
//                    println("(poi.x - b.px)/b.vx = ${(poi.x - b.px)/b.vx}")
//                    println("(poi.y - b.py)/b.vy = ${(poi.y - b.py)/b.vy}")
                    }
                }
            }
        }

        return intersectingHailstones.size
    }

    fun part2(input: List<String>): Int {
        return 1
    }

//    val testInput = readInput("Day24_test")
//    check(part1(testInput, 7L, 27L) == 2)
    val input = readInput("Day24")
    part1(input, 200_000_000_000_000.toDouble(), 400000000000000L.toDouble()).println()

//    val testInput2 = readInput("Day24_test")
//    check(part2(testInput2) == 281)
//    val input2 = readInput("Day24")
//    part2(input2).println()

}

//assumes no two hailstones have the same position and velocity (else we need a diff equals implementation)
data class Hailstone(val px: Double, val py: Double, val pz: Double,
                     val vx: Double, val vy: Double, val vz: Double) {

}
