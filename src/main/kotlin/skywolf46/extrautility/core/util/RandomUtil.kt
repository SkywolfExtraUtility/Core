package skywolf46.extrautility.core.util

import java.util.*

object RandomUtil {
    private val internalRandom = Random()

    fun nextInt(min: Int, max: Int): Int {
        if (max < min)
            return nextInt(max, min)
        return (min + (max - min) * internalRandom.nextDouble()).toInt()
    }

    fun nextInt(max: Int) = nextInt(0, max)

    fun nextFloat(min: Float, max: Float): Float {
        if (max < min)
            return nextFloat(max, min)
        return (min + (max - min) * internalRandom.nextDouble()).toFloat()
    }

    fun nextFloat(max: Float) = nextFloat(0f, max)

    fun nextDouble(min: Double, max: Double): Double {
        if (max < min)
            return nextDouble(max, min)
        return (min + (max - min) * internalRandom.nextDouble())
    }

    fun nextDouble(max: Double) = nextDouble(0.0, max)

    fun nextLong(min: Long, max: Long): Long {
        if (max < min)
            return nextLong(max, min)
        return (min + (max - min) * internalRandom.nextDouble()).toLong()
    }

    fun nextLong(max: Long) = nextLong(0L, max)

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
            return priorityMap.floorEntry(nextLong(total))?.value
        }
    }
}