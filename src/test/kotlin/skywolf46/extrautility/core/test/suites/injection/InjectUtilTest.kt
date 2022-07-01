package skywolf46.extrautility.core.test.suites.injection

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import skywolf46.extrautility.core.test.abstraction.CoreRequiredTest
import skywolf46.extrautility.core.test.data.TestConfig
import skywolf46.extrautility.core.test.data.TestExtraClass
import skywolf46.extrautility.core.util.InjectionUtil

class InjectUtilTest : CoreRequiredTest() {
    @Test
    fun test() {
        assertEquals(
            InjectionUtil.get(TestConfig::class.java),
            InjectionUtil.get(TestConfig::class.java)
        )
        assertNotEquals(
            InjectionUtil.get(TestExtraClass::class.java),
            InjectionUtil.get(TestExtraClass::class.java)
        )
        assertTrue(InjectionUtil.instantiate(TestConfig::class.java)!!.test.isTestCompleted)
    }
}