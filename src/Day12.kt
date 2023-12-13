fun main() {

    fun parseInput(input: List<String>): List<SpringRecords> {
        return input.map {
            val (springs, record) = it.split(" ")
            val records = record.split(",").map { it.trim().toInt() }
//            println("springs = ${springs}")
//            println("records = ${records}")
            SpringRecords(springs, records)
        }

    }

    fun generatePermutationsHelper(
        characters: List<Char>,
        combinationLength: Int,
        currentCombination: String,
        result: MutableList<String>
    ) {
        if (combinationLength == 0) {
            result.add(currentCombination)
            return
        }

        for (char in characters) {
            generatePermutationsHelper(
                characters,
                combinationLength - 1,
                "$currentCombination$char",
                result
            )
        }
    }

    fun generatePermutations(characters: List<Char>, combinationLength: Int): List<String> {
        val result = mutableListOf<String>()

        generatePermutationsHelper(characters, combinationLength, "", result)

        return result
    }


    fun generateRegExp(records: List<Int>): Regex {
        //^[a]+[b]{1}[a]+[b]{1}[a]+[b]{3}[a]+$
        return records
            .map {
                "[#]{$it}"
            }
            .joinToString("[\\.]+", "^[\\.]*", "[\\.]*$")
            .toRegex()
    }

    fun part1(input: List<String>): Int {
        val springRecords = parseInput(input)

        return springRecords.sumOf { springRecord ->
            val regexp = generateRegExp(springRecord.records)

            //brute force
            val springs = springRecord.springs
            val totalQs = springs.count { c: Char -> c == '?' }

            val permutations = generatePermutations(listOf('#', '.'), totalQs)
            permutations
                .map { candidate ->
                    //replace each '?' with the corresponding char in the candidate
                    var newSprings = springs
                    candidate.forEach { c ->
                        newSprings = newSprings.replaceFirst('?', c)
                    }
                    newSprings
                }.count { alternative ->
                    regexp.matches(alternative)
                }
        }
    }


    //Doesn't work
    fun part2(input: List<String>): Int {
        var springRecords = parseInput(input)


        return springRecords.sumOf { springRecord ->
            val regexp = generateRegExp(springRecord.records)

            //brute force
            val springs = springRecord.springs
            val totalQs = springs.count { c: Char -> c == '?' }

            val permutations = generatePermutations(listOf('#', '.'), totalQs)
            permutations
                .map { candidate ->
                    //replace each '?' with the corresponding char in the candidate
                    var newSprings = springs
                    candidate.forEach { c ->
                        newSprings = newSprings.replaceFirst('?', c)
                    }
                    newSprings
                }.count { alternative ->
                    regexp.matches(alternative)
                }
        }
    }

//    val testInput = readInput("Day12_test")
//    check(part1(testInput) == 21)
//    val input = readInput("Day12")
//    part1(input).println()

    val testInput2 = readInput("Day12_test")
    check(part2(testInput2) == 525152)
//    val input2 = readInput("Day12")
//    part2(input2).println()

}

data class SpringRecords(val springs: String, val records: List<Int>)
