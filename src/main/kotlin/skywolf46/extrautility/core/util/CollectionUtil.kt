package skywolf46.extrautility.core.util


fun <T : Any> MutableList<T>.append(data: T): MutableList<T> {
    add(data)
    return this
}

inline fun <T : Any> Iterable<T>.allIndexed(unit: T.(Int) -> Boolean): Boolean {
    if (this is Collection && isEmpty())
        return true
    forEachIndexed { index, t ->
        if (!unit(t, index))
            return false
    }
    return true
}


inline fun <T : Any> Iterable<T>.anyIndexed(unit: T.(Int) -> Boolean): Boolean {
    if (this is Collection && isEmpty())
        return false
    forEachIndexed { index, t ->
        if (unit(t, index))
            return true
    }
    return false
}

inline fun <T : Any> Iterable<T>.noneIndexed(unit: T.(Int) -> Boolean): Boolean {
    if (this is Collection && isEmpty())
        return true
    forEachIndexed { index, t ->
        if (unit(t, index))
            return false
    }
    return true
}


inline fun <T : Any> Array<out T>.allIndexed(unit: T.(Int) -> Boolean): Boolean {
    if (isEmpty())
        return true
    forEachIndexed { index, t ->
        if (!unit(t, index))
            return false
    }
    return true
}

fun <T : Any> Array<out T>.anyIndexed(unit: T.(Int) -> Boolean): Boolean {
    if (isEmpty())
        return false
    forEachIndexed { index, t ->
        if (unit(t, index))
            return true
    }
    return false
}

inline fun <T : Any> Array<out T>.noneIndexed(unit: T.(Int) -> Boolean): Boolean {
    if (isEmpty())
        return true
    forEachIndexed { index, t ->
        if (unit(t, index))
            return false
    }
    return true
}

