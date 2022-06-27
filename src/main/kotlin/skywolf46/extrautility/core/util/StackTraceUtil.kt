package skywolf46.extrautility.core.util

object StackTraceUtil {
    private val className = javaClass.name
    fun findLastStackTrace(ignoredPackages: List<String>): StackTraceElement? {
        val ignored = ArrayList<String>(ignoredPackages)
        ignored += className
        val stackTrace = Throwable().stackTrace
        for (trace in stackTrace) {
            if (ignored.all { !trace.className.startsWith(it) }) {
                return trace
            }
        }
        return null
    }
}