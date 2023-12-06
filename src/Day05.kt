fun main() {
    fun processSeeds(currLine: String): List<Long> {
        return currLine
            .substringAfter("seeds:")
            .split(" ")
            .filter { it.isNotBlank() }
            .map { it.trim().toLong() }
    }

    fun processSeedsPart2(currLine: String): List<LongRange> = currLine
        .substringAfter("seeds:")
        .split(" ")
        .filter { it.isNotBlank() }
        .map { it.trim().toLong() }
        .windowed(2, 2)
        .map {
            val (start, range) = it
            (start..<(start + range))
        }

    fun extractLines(iterator: Iterator<String>): MutableList<String> {
        val mappingList = mutableListOf<String>()
        var endOfSection = false
        while (iterator.hasNext() && !endOfSection) {
            var currLine = iterator.next()
            if (currLine.isBlank()) {
                endOfSection = true
            } else {
                //collect info lines
                mappingList.add(currLine.trim())
            }
        }
        return mappingList
    }

    fun processMapping(iterator: Iterator<String>): List<SourceDestMap> {
        val lines = extractLines(iterator)

        return lines.map { line ->
            val (destinationRangeStart, sourceRangeStart, rangeLength) = line.split(" ").map { it.trim().toLong() }

            val sourceRange: LongRange = sourceRangeStart..<(sourceRangeStart + rangeLength)
            val destinationRange: LongRange = (destinationRangeStart..<(destinationRangeStart + rangeLength))

            SourceDestMap(sourceRange, destinationRange)
        }
    }

    fun parseInput(input: List<String>): Almanac {
        var seeds: List<Long> = listOf()
        var seed2Soil: List<SourceDestMap> = listOf()
        var soil2Fertilizer: List<SourceDestMap> = listOf()
        var fertilizer2Water: List<SourceDestMap> = listOf()
        var water2Light: List<SourceDestMap> = listOf()
        var light2Temperature: List<SourceDestMap> = listOf()
        var temperature2Humidity: List<SourceDestMap> = listOf()
        var humidity2Location: List<SourceDestMap> = listOf()

        val iterator = input.iterator()
        while (iterator.hasNext()) {
            val currLine = iterator.next()

            when {
                currLine.startsWith("seeds:") -> {
                    seeds = processSeeds(currLine)
                }

                currLine.startsWith("seed-to-soil map:") -> {
                    seed2Soil = processMapping(iterator)
                }

                currLine.startsWith("soil-to-fertilizer map:") -> {
                    soil2Fertilizer = processMapping(iterator)
                }

                currLine.startsWith("fertilizer-to-water map:") -> {
                    fertilizer2Water = processMapping(iterator)
                }

                currLine.startsWith("water-to-light map:") -> {
                    water2Light = processMapping(iterator)
                }

                currLine.startsWith("light-to-temperature map:") -> {
                    light2Temperature = processMapping(iterator)
                }

                currLine.startsWith("temperature-to-humidity map:") -> {
                    temperature2Humidity = processMapping(iterator)
                }

                currLine.startsWith("humidity-to-location map:") -> {
                    humidity2Location = processMapping(iterator)
                }
            }

        }
        return Almanac(
            seeds, listOf(
                seed2Soil, soil2Fertilizer, fertilizer2Water,
                water2Light, light2Temperature, temperature2Humidity, humidity2Location
            )
        )
    }

    fun parseInputPart2(input: List<String>): AlmanacPart2 {
        var seeds: List<LongRange> = listOf()
        var seed2Soil: List<SourceDestMap> = listOf()
        var soil2Fertilizer: List<SourceDestMap> = listOf()
        var fertilizer2Water: List<SourceDestMap> = listOf()
        var water2Light: List<SourceDestMap> = listOf()
        var light2Temperature: List<SourceDestMap> = listOf()
        var temperature2Humidity: List<SourceDestMap> = listOf()
        var humidity2Location: List<SourceDestMap> = listOf()

        val iterator = input.iterator()
        while (iterator.hasNext()) {
            val currLine = iterator.next()

            when {
                currLine.startsWith("seeds:") -> {
                    seeds = processSeedsPart2(currLine)
                }

                currLine.startsWith("seed-to-soil map:") -> {
                    seed2Soil = processMapping(iterator)
                }

                currLine.startsWith("soil-to-fertilizer map:") -> {
                    soil2Fertilizer = processMapping(iterator)
                }

                currLine.startsWith("fertilizer-to-water map:") -> {
                    fertilizer2Water = processMapping(iterator)
                }

                currLine.startsWith("water-to-light map:") -> {
                    water2Light = processMapping(iterator)
                }

                currLine.startsWith("light-to-temperature map:") -> {
                    light2Temperature = processMapping(iterator)
                }

                currLine.startsWith("temperature-to-humidity map:") -> {
                    temperature2Humidity = processMapping(iterator)
                }

                currLine.startsWith("humidity-to-location map:") -> {
                    humidity2Location = processMapping(iterator)
                }
            }

        }
        return AlmanacPart2(
            seeds, listOf(
                seed2Soil, soil2Fertilizer, fertilizer2Water,
                water2Light, light2Temperature, temperature2Humidity, humidity2Location
            )
        )
    }

    fun lookupSeedToLocation(almanac: Almanac) = almanac.seeds.minOf { seedNumber ->
        var currLookupValue = seedNumber
        for (sourceDestMap in almanac.seeds2Locations) {
            val foundMap = sourceDestMap.firstOrNull { currLookupValue in it.sourceRange }
            if (foundMap != null) {
                currLookupValue = foundMap.destinationRange.start + (currLookupValue - foundMap.sourceRange.start)
            }
        }
        currLookupValue
    }

    fun mapToDestinationRanges(ranges: List<LongRange>, sourceDestMapList: List<SourceDestMap>): List<LongRange> {
        val bigDestinationRanges = mutableSetOf<LongRange>()

        ranges.forEach { range ->
            val subRanges = mutableListOf(range)
            val mappedSubRanges = mutableListOf<LongRange>()
            val destinationRanges = mutableListOf<LongRange>()

            while (subRanges.isNotEmpty()) {
                val oneRange = subRanges[0]
                var foundOverlap = false
                sourceDestMapList.forEach { sourceDestMap ->
                    val sourceRange = sourceDestMap.sourceRange
                    val destinationRange = sourceDestMap.destinationRange
                    val destinationOverlapRange: LongRange?
                    val overlapRange: LongRange?
                    if (oneRange.start <= sourceRange.endInclusive && oneRange.endInclusive >= sourceRange.start) {
                        //overlap
                        overlapRange =
                            (maxOf(oneRange.start, sourceRange.start)..minOf(oneRange.endInclusive, sourceRange.endInclusive))
                        mappedSubRanges.add(overlapRange)

                        //find the non-overlapped ranges
                        if (oneRange.start < overlapRange.start) {
                            //overlaps on the left
                            val newRange1 = oneRange.start..overlapRange.start-1
                            subRanges.add(newRange1)
                        } else if (oneRange.endInclusive > overlapRange.endInclusive) {
                            //overlaps on the right
                            val newRange2 = overlapRange.endInclusive+1..oneRange.endInclusive
                            subRanges.add(newRange2)
                        }
                        foundOverlap = true

                        //get the destination ranges
                        val diffBetweenSourceAndDestRanges = destinationRange.start - sourceRange.start
                        destinationOverlapRange =
                            (overlapRange.start + diffBetweenSourceAndDestRanges)..(overlapRange.endInclusive + diffBetweenSourceAndDestRanges)
                        destinationRanges.add(destinationOverlapRange)
                    }
                }

                if (!foundOverlap) {
                    destinationRanges.add(oneRange)
                }

                //finally remove
                subRanges.remove(oneRange)
            }

            if (mappedSubRanges.isEmpty()) {
                //if nothing matched then add the range as-is
                destinationRanges.add(range)
            }

            //final list
            bigDestinationRanges.addAll(destinationRanges)
        }

        return bigDestinationRanges.toList()
    }

    fun lookupSeedToLocationPart2(almanac: AlmanacPart2): Long {

        var soils = mapToDestinationRanges(almanac.seeds, almanac.seeds2Locations[0])
        var fertilizers = mapToDestinationRanges(soils, almanac.seeds2Locations[1])
        var waters = mapToDestinationRanges(fertilizers, almanac.seeds2Locations[2])
        var lights = mapToDestinationRanges(waters, almanac.seeds2Locations[3])
        var temperatures = mapToDestinationRanges(lights, almanac.seeds2Locations[4])
        var humidities = mapToDestinationRanges(temperatures, almanac.seeds2Locations[5])
        var locations = mapToDestinationRanges(humidities, almanac.seeds2Locations[6])

        return locations.minOf {
            it.start
        }
    }

    fun part1(input: List<String>): Long {
        val almanac = parseInput(input)

        //lookup
        return lookupSeedToLocation(almanac)
    }

    fun part2(input: List<String>): Long {
        var almanac = parseInputPart2(input)

        //lookup
        var location = lookupSeedToLocationPart2(almanac)
        return location
    }

//    val testInput = readInput("Day05_test")
//    check(part1(testInput) == 35.toLong())
//    val input = readInput("Day05")
//    part1(input).println()

    val testInput2 = readInput("Day05_test")
    check(part2(testInput2) == 46.toLong())
    val input2 = readInput("Day05")
    part2(input2).println()

}

data class Almanac(
    val seeds: List<Long>,
    val seeds2Locations: List<List<SourceDestMap>>
)

data class AlmanacPart2(
    val seeds: List<LongRange>,
    val seeds2Locations: List<List<SourceDestMap>>
)

data class SourceDestMap(val sourceRange: LongRange, val destinationRange: LongRange)
