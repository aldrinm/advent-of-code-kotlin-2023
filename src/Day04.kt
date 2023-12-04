fun main() {

    fun parseInput(input: List<String>): List<Card> {
        return input.map {
            val (cardNumStr, numbersStr) = it.split(":")
            val cardNumber = cardNumStr.substringAfter("Card").trim().toInt()

            val (winningNumberStr, myNumbersStr) = numbersStr.split("|")
            val winningNumbers = winningNumberStr
                .split(" ")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            val myNumbers = myNumbersStr
                .split(" ")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            Card(cardNumber, winningNumbers, myNumbers)
        }
    }

    fun part1(input: List<String>): Int {
        val cards = parseInput(input)
        return cards.sumOf { card ->
            val commonNumbers = card.winningNumbers
                .filter { card.myNumbers.contains(it) }
            Math.pow(2.toDouble(), (commonNumbers.size - 1).toDouble()).toInt()
        }
    }

    fun part2(input: List<String>): Int {
        val cards = parseInput(input)

        var i = 0
        while (i < cards.size) {
            println("i = ${i}")
            val card = cards[i]
            val commonNumbers = card.winningNumbers
                .filter { card.myNumbers.contains(it) }
            if ((commonNumbers.size + i) >= cards.size) {
                throw RuntimeException("Unexpected winners for card #${i}. Seems more than the remaining number of cards")
            }
            (1..commonNumbers.size).forEach { ind ->
                cards[i + ind].copies += card.copies
            }
            i++
        }

        return cards.sumOf { it.copies }
    }

    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    val input = readInput("Day04")
    part1(input).println()

    val testInput2 = readInput("Day04_test")
    check(part2(testInput2) == 30)
    val input2 = readInput("Day04")
    part2(input2).println()

}

data class Card(val cardNumber: Int, val winningNumbers: List<String>, val myNumbers: List<String>, var copies: Int = 1)
