package skywolf46.extrautility.core.abstraction

interface JvmFilter<T : Any> {
    fun isSatisfied(data: T): Boolean
}