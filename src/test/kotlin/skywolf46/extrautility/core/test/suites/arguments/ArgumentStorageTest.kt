package skywolf46.extrautility.core.test.suites.arguments

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import skywolf46.extrautility.core.data.ArgumentStorage

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ArgumentStorageTest {
    companion object {
        private val sharedArgument = ArgumentStorage()
        private const val testKey = "testStringKey"
        private const val testString = "testString123"
    }

    @Test
    @Order(0)
    fun testNonExists() {
        assertFalse(sharedArgument.contains(String::class.java))
        assertFalse(sharedArgument.contains(testString))
        assertTrue(sharedArgument.getAll(String::class.java).isEmpty())
        assertNull(sharedArgument[testKey])
        assertNull(sharedArgument[String::class.java])
    }

    @Test
    @Order(1)
    fun testInsertion() {
        assertNull(sharedArgument.replace(testKey, testString))
        assertNotNull(sharedArgument.replace(testKey, testString))
        assertEquals(testString, sharedArgument.replace(testKey, testString))
        assertEquals(testString, sharedArgument[testKey])
        assertNull(sharedArgument[String::class.java])
        sharedArgument += testString
        sharedArgument += testString
        assertEquals(testString, sharedArgument[String::class.java])
        assertEquals(2, sharedArgument.getAll(String::class.java).size)
    }

    @Test
    @Order(2)
    fun testGet() {
        assertEquals(testString, sharedArgument.find<String>())
        assertTrue(sharedArgument.findAll<String>().isNotEmpty())
        assertTrue(sharedArgument.findAll<Int>().isEmpty())
        assertTrue(sharedArgument.getAll(Int::class.java).isEmpty())
        assertThrows<NullPointerException> {
            sharedArgument.find<Int>()
        }
    }

    @Test
    @Order(3)
    fun testRemoval() {
        assertNotNull(sharedArgument[testKey])
        sharedArgument.removeKey(testKey)
        assertNull(sharedArgument[testKey])
        assertNotNull(sharedArgument[String::class.java])
        sharedArgument -= testString
        assertNotNull(sharedArgument[String::class.java])
        assertEquals(1, sharedArgument.getAll(String::class.java).size)
        sharedArgument.removeAll(String::class.java)
        assertNull(sharedArgument[String::class.java])
        sharedArgument.removeAll(Int::class.java)
        sharedArgument.remove("NonExistsArgument")
    }

    @Test
    @Order(4)
    fun testClear() {
        sharedArgument[testKey] = testString
        sharedArgument += testString
        sharedArgument += testString
        sharedArgument += testString
        sharedArgument.clear()
        assertEquals(0, sharedArgument.getAll(String::class.java).size)
        assertNull(sharedArgument[testKey])
        assertNull(sharedArgument[String::class.java])
    }

    @Test
    @Order(5)
    fun testClone() {
        sharedArgument[testKey] = testString
        sharedArgument += testString
        sharedArgument += testString
        sharedArgument += testString
        val copy = sharedArgument.clone()
        val copyOnlyName = sharedArgument.copy(copyNamedArgs = true, copyClassArgs = false)
        val copyOnlyClass = sharedArgument.copy(copyNamedArgs = false, copyClassArgs = true)
        val transfer = sharedArgument.copy(copyNamedArgs = false, copyClassArgs = false)

        // Full copy test
        copy.removeKey(testKey)
        copy.removeAll(String::class.java)
        assertNull(copy[String::class.java])
        assertNotNull(sharedArgument[String::class.java])
        assertNotNull(sharedArgument[testKey])

        // Name copy test
        copyOnlyName.removeKey(testKey)
        assertNull(copyOnlyName[testKey])
        assertNotNull(sharedArgument[testKey])

        // Class copy test
        copyOnlyClass.removeAll(String::class.java)
        assertNull(copyOnlyClass.findNullable<String>())
        assertNotNull(sharedArgument.findNullable<String>())

        // Transfer test
        transfer.removeAll(String::class.java)
        assertEquals(transfer.findAll<String>().size, sharedArgument.findAll<String>().size)
    }

}