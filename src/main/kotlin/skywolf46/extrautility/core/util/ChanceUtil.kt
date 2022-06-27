package skywolf46.extrautility.core.util

import java.util.*

object ChanceUtil {
    private val internalRandom = Random()

    fun next(min: Long, max: Long): Long {
        if (max < min)
            return next(max, min)
        return (min + (max - min) * internalRandom.nextDouble()).toLong()
    }

    fun next(max: Long) = next(0, max)


    fun <T : Any> newRandomizer(): Randomizer<T> {
        return Randomizer()
    }

    class Randomizer<T : Any> {
        private val priorityMap = TreeMap<Long, T>()
        private var total = 0L

        fun add(priority: Int, data: T) {
            priorityMap[total] = data
            total += priority
        }

        fun next(): T? {
            return priorityMap.ceilingEntry(next(total))?.value
        }
    }
}