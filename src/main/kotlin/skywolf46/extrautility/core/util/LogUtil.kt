package skywolf46.extrautility.core.util

import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

internal object LogUtil {
    val classFilter = listOf("${this::class.jvmName}Kt")
}

private val prefixes = mutableMapOf<String, String>()

fun bindPrefixTo(cls: Class<*>, prefix: String) {
    prefixes[cls.name] = prefix
}

fun bindPrefixTo(cls: KClass<*>, prefix: String) {
    bindPrefixTo(cls.java, prefix)
}
fun bindPrefix(prefix: String) {
    bindPrefix(StackTraceUtil.findLastStackTrace(LogUtil.classFilter)!!.className, prefix)
}

fun bindPrefix(classPackage: String, prefix: String) {
    prefixes[classPackage] = prefix
}

fun findPrefix(): String? {
    val packageName =
        StackTraceUtil.findLastStackTrace(LogUtil.classFilter)!!.className
    return findPrefixFor(packageName)
}

fun findPrefixFor(packageName: String): String? {
    val splitter = packageName.lastIndexOf('.')
    if (splitter == -1)
        return prefixes[packageName]
    return prefixes[packageName] ?: findPrefixFor(packageName.substring(0, splitter))
}

fun findPrefixFor(cls: Class<*>) : String? {
    return findPrefixFor(cls.name)
}

fun findPrefixFor(kls: KClass<*>) : String? {
    return findPrefixFor(kls.java)
}

fun appendPrefix(msg: String): String {
    return (findPrefix() ?: "") + msg
}

fun log(msg: String) {
    println(appendPrefix(msg))
}

fun logError(msg: String) {
    System.err.println(appendPrefix(msg))
}

