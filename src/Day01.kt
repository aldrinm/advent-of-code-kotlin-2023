fun main() {

    val wordToDigit = mapOf(
        "one" to "1",
        "two" to "2",
        "three" to "3",
        "four" to "4",
        "five" to "5",
        "six" to "6",
        "seven" to "7",
        "eight" to "8",
        "nine" to "9",
    )

    fun extractTwoDigits(it: String): Int {
        val firstDigit: Char? = it.firstOrNull { it.isDigit() }
        val lastDigit: Char? = it.lastOrNull { it.isDigit() }
        val combinedDigits = if (firstDigit != null && lastDigit != null) {
            (firstDigit.toString() + lastDigit.toString()).toInt()
        } else {
            null
        }
        return combinedDigits ?: 0
    }

    fun part1(input: List<String>): Int {
        return input.sumOf {
            extractTwoDigits(it)
        }
    }

    fun findFirstDigitOrWord(str: String): String? {
        str.forEachIndexed { index, c ->
            if (c.isDigit()) {
                return c.toString()
            }
            (3..5).forEach { len ->
                if ((index + len) <= str.length) {
                    var candidate = str.substring(index, index + len)
                    if (wordToDigit.get(candidate) != null) {
                        return wordToDigit.get(candidate)
                    }
                }
            }
        }
        return null
    }

    fun findLastDigitOrWord(str: String): String? {
        str.indices.reversed().forEach { index->
            val c = str[index]
            if (c.isDigit()) {
                return c.toString()
            }
            (3..5).forEach { len ->
                if ((index + len) <= str.length) {
                    var candidate = str.substring(index, index + len)
                    if (wordToDigit.get(candidate) != null) {
                        return wordToDigit.get(candidate)
                    }
                }
            }

        }
        return null
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { str->
            val firstDigit = findFirstDigitOrWord(str)

            val lastDigit = findLastDigitOrWord(str)

            val combinedDigits = if (firstDigit != null && lastDigit != null) {
                (firstDigit.toString() + lastDigit.toString()).toInt()
            } else {
                null
            }
            combinedDigits?:0
        }
    }

    val testInput = readInput("Day01_part1_test")
    check(part1(testInput) == 142)
    val input = readInput("Day01")
    part1(input).println()

    val testInput2 = readInput("Day01_part2_test")
    check(part2(testInput2) == 281)
    val input2 = readInput("Day01")
    part2(input2).println()

}
