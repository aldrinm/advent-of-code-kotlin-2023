fun main() {

    fun determineKind(cards: String): HandType {
        val distinctCount = cards.codePoints().distinct().count()
        if (distinctCount == 1L) {
            return HandType.FIVE_OF_A_KIND
        } else if (distinctCount == 2L) {
            val groupBy = cards.groupBy { it }
            if (groupBy.any { entry -> entry.value.size >= 4 }) {
                return HandType.FOUR_OF_A_KIND
            } else {
                return HandType.FULL_HOUSE
            }
        } else if (distinctCount == 3L) {
            val groupBy = cards.groupBy { it }
            return if (groupBy.any { entry -> entry.value.size >= 3 }) {
                HandType.THREE_OF_A_KIND
            } else {
                HandType.TWO_PAIR
            }
        } else if (distinctCount == 4L) {
            return HandType.ONE_PAIR
        } else {
            return HandType.HIGH_CARD
        }
    }

    fun parseInput(input: List<String>): List<Hand> {
        return input.map {
            val (cards, bid) = it.split(" ")
            Hand(cards, bid.toInt(), determineKind(cards))
        }
    }

    fun compareLabels(c1: Char, c2: Char): Int {
        val labelValues = mapOf(
            '2' to 2,
            '3' to 3,
            '4' to 4,
            '5' to 5,
            '6' to 6,
            '7' to 7,
            '8' to 8,
            '9' to 9,
            'T' to 10,
            'J' to 11,
            'Q' to 12,
            'K' to 13,
            'A' to 14,
            )
        if (labelValues.containsKey(c1) && labelValues.containsKey(c2)) {
            return labelValues[c1]!!.compareTo(labelValues[c2]!!)
        } else {
            throw RuntimeException("Could not compare these char labels: ${c1} and ${c2} ")
        }
    }

    fun compareLabelsPart2(c1: Char, c2: Char): Int {
        val labelValues = mapOf(
            'J' to 1,
            '2' to 2,
            '3' to 3,
            '4' to 4,
            '5' to 5,
            '6' to 6,
            '7' to 7,
            '8' to 8,
            '9' to 9,
            'T' to 10,
            'Q' to 12,
            'K' to 13,
            'A' to 14,
            )
        if (labelValues.containsKey(c1) && labelValues.containsKey(c2)) {
            return labelValues[c1]!!.compareTo(labelValues[c2]!!)
        } else {
            throw RuntimeException("Could not compare these char labels: ${c1} and ${c2} ")
        }
    }

    fun compareCardByCard(card1: String, cards2: String): Int {
        card1.forEachIndexed { index, c ->
            if (c != cards2[index]) {
                return compareLabels(c, cards2[index])
            }
        }
        return 0
    }

    fun compareCardByCardPart2(card1: String, cards2: String): Int {
        card1.forEachIndexed { index, c ->
            if (c != cards2[index]) {
                return compareLabelsPart2(c, cards2[index])
            }
        }
        return 0
    }

    fun part1(input: List<String>): Int {
        val hands = parseInput(input)

        //rank the hands by rank descending
        hands.sortedWith(Comparator { o1, o2 ->
                if (o1.type.value == o2.type.value) {
                    compareCardByCard(o1.cards, o2.cards)
                } else {
                    o1.type.value.compareTo(o2.type.value)
                }
            }).forEachIndexed { index, hand -> hand.rank = index + 1 }

        return hands.sumOf { hand ->
            hand.rank * hand.bid
        }.also(::println)
    }

    fun generateCombos(card: String): List<String> {
        if (card.contains('J')) {
            return listOf("A", "K", "Q", "T", "9", "8", "7", "6", "5", "4", "3", "2")
                .flatMap {
                    val newCard = card.replaceFirst("J", it)
                    generateCombos(newCard)
                }
        } else {
            return listOf(card)
        }
    }

    fun determineBestJokers(card: String): Pair<String, HandType> {
        val combos = generateCombos(card)
        val maxBy = combos.map {
            Pair(it, determineKind(it))
        }.maxBy { pair ->
            pair.second.value
        }
        return maxBy
    }

    fun part2(input: List<String>): Int {
        val hands = parseInput(input)

        hands.forEach { hand ->
            hand.jokersReplaced = determineBestJokers(hand.cards)
        }

        //rank the hands by rank descending
        hands.sortedWith(Comparator { o1, o2 ->
            if (o1.jokersReplaced.second.value == o2.jokersReplaced.second.value) {
                compareCardByCardPart2(o1.cards, o2.cards)
            } else {
                o1.jokersReplaced.second.compareTo(o2.jokersReplaced.second)
            }
        }).forEachIndexed { index, hand -> hand.rank = index + 1 }

        return hands.sumOf { hand ->
            hand.rank * hand.bid
        }.also(::println)

    }

//    val testInput = readInput("Day07_test")
//    check(part1(testInput) == 6440)
//    val input = readInput("Day07")
//    part1(input).println()

//    val testInput2 = readInput("Day07_test")
//    check(part2(testInput2) == 5905)
    val input2 = readInput("Day07")
    part2(input2).println()

}

data class Hand(val cards: String, val bid: Int, val type: HandType, var rank: Int = 0, var jokersReplaced: Pair<String, HandType> = Pair("", HandType.HIGH_CARD))

enum class HandType(val value: Int) {
    HIGH_CARD(1),
    ONE_PAIR(2),
    TWO_PAIR(3),
    THREE_OF_A_KIND(4),
    FULL_HOUSE(5),
    FOUR_OF_A_KIND(6),
    FIVE_OF_A_KIND(7)
}
