package skywolf46.extrautility.core.test.suites.collections

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import skywolf46.extrautility.core.util.allIndexed
import skywolf46.extrautility.core.util.anyIndexed
import skywolf46.extrautility.core.util.append
import skywolf46.extrautility.core.util.noneIndexed

class CollectionUtilTest {

    @Test
    fun appendTest() {
        val list = mutableListOf<String>()
        list.append("Test1")
            .append("Test2")
        assertEquals(2, list.size)
    }

    @Test
    fun emptyCollectionTest() {
        // List test
        assertTrue(emptyList<String>().noneIndexed { contains(" ") })
        assertTrue(emptyList<String>().allIndexed { contains(" ") })
        assertFalse(emptyList<String>().anyIndexed { contains(" ") })

        // Array test
        assertTrue(emptyArray<String>().noneIndexed { contains(" ") })
        assertTrue(emptyArray<String>().allIndexed { contains(" ") })
        assertFalse(emptyArray<String>().anyIndexed { contains(" ") })
    }

    @Test
    fun filledListTest() {
        val list = listOf("Lorem Ipsum", "Test string")
        assertTrue(list.allIndexed { contains(" ") })
        assertTrue(list.anyIndexed { contains(" ") })
        assertFalse(list.noneIndexed { contains(" ") })
    }
}