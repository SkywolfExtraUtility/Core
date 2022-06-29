package skywolf46.extrautility.core.abstraction

import java.lang.reflect.AnnotatedElement

interface JvmFilter<T : AnnotatedElement> {
    fun isSatisfied(data: T): Boolean
}