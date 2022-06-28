package skywolf46.extrautility.core.test.suites.random

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import skywolf46.extrautility.core.util.RandomUtil

class RandomUtilTest {

    companion object {
        private const val minValueReversed = -10000L
        private const val minValue = -1000L
        private const val maxValue = 100000L
    }

    @Test
    fun testNormalRandom() {
        val randomInt = RandomUtil.nextInt(minValue.toInt(), maxValue.toInt())
        val randomFloat = RandomUtil.nextFloat(minValue.toFloat(), maxValue.toFloat())
        val randomDouble = RandomUtil.nextDouble(minValue.toDouble(), maxValue.toDouble())
        val randomLong = RandomUtil.nextLong(minValue, maxValue)
        assertTrue(randomInt in minValue.toInt()..maxValue.toInt())
        assertTrue(randomFloat in minValue.toFloat()..maxValue.toFloat())
        assertTrue(randomDouble in minValue.toDouble()..maxValue.toDouble())
        assertTrue(randomLong in minValue..maxValue)
    }

    @Test
    fun testReversedRandom() {
        val randomInt = RandomUtil.nextInt(minValueReversed.toInt())
        val randomFloat = RandomUtil.nextFloat(minValueReversed.toFloat())
        val randomDouble = RandomUtil.nextDouble(minValueReversed.toDouble())
        val randomLong = RandomUtil.nextLong(minValueReversed)
        assertTrue(randomInt in minValueReversed.toInt()..0)
        assertTrue(randomFloat in minValueReversed.toFloat()..0f)
        assertTrue(randomDouble in minValueReversed.toDouble()..0.0)
        assertTrue(randomLong in minValueReversed..0L)
    }

    @Test
    fun testNullRandom() {
        assertNull(RandomUtil.newRandomizer<Any>().next())
    }

    @Test
    fun testSingleRandom() {
        val randomizer = RandomUtil.newRandomizer<Any>()
        randomizer.add(400, "Test1")
        assertNotNull(randomizer.next())
    }

    @Test
    fun testMultipleRandom() {
        val randomizer = RandomUtil.newRandomizer<Any>()
        randomizer.add(400, "Test1")
        randomizer.add(400, "Test2")
        randomizer.add(400, "Test3")
        assertNotNull(randomizer.next())
    }
}