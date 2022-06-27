package skywolf46.extrautility.core.util

import kotlin.reflect.jvm.jvmName

internal object LogUtil {
    val className: String = "${this::class.jvmName}Kt"
}


private val prefixes = mutableMapOf<String, String>()

fun bindPrefix(classPackage: String, prefix: String) {
    prefixes[classPackage] = prefix
}

fun findPrefix(): String? {
    val packageName =
        findLastStackTrace()?.className?.substringBeforeLast('.')
            ?: return null
    return findPrefixFor(packageName)
}

fun findPrefixFor(packageName: String): String? {
    val splitter = packageName.lastIndexOf('.')
    if (splitter == -1)
        return prefixes[packageName]
    return prefixes[packageName] ?: findPrefixFor(packageName.substring(0, splitter))
}

fun appendPrefix(msg: String): String {
    return (findPrefix() ?: "") + msg
}

private fun findLastStackTrace(): StackTraceElement? {
    val stackTrace = Throwable().stackTrace
    for (trace in stackTrace) {
        if (trace.className != LogUtil.className) {
            return trace
        }
    }
    return null
}

fun log(msg: String) {
    println(appendPrefix(msg))
}

fun logError(msg: String) {
    System.err.println(appendPrefix(msg))
}