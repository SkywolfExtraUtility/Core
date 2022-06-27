package skywolf46.extrautility.core.util

fun <T : Any> Iterable<T>.allIndexed(unit: T.(Int) -> Boolean): Boolean {
    if (this is Collection && isEmpty())
        return true
    forEachIndexed { index, t ->
        if (!unit(t, index))
            return false
    }
    return true
}


fun <T : Any> Iterable<T>.noneIndexed(unit: T.(Int) -> Boolean): Boolean {
    if (this is Collection && isEmpty())
        return true
    forEachIndexed { index, t ->
        if (unit(t, index))
            return false
    }
    return true
}


fun <T : Any> Iterable<T>.anyIndexed(unit: T.(Int) -> Boolean): Boolean {
    if (this is Collection && isEmpty())
        return true
    forEachIndexed { index, t ->
        if (unit(t, index))
            return true
    }
    return false
}