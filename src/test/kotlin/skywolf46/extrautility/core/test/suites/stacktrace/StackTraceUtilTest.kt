package skywolf46.extrautility.core.test.suites.stacktrace

import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import skywolf46.extrautility.core.util.StackTraceUtil

class StackTraceUtilTest {
    @Test
    fun testAllDeniedStackTrace() {
        assertNull(StackTraceUtil.findLastStackTrace(mutableListOf("")))
    }
}