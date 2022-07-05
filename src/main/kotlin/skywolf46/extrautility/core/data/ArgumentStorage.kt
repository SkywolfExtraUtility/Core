@file:Suppress("UNCHECKED_CAST")

package skywolf46.extrautility.core.data

import skywolf46.extrautility.core.util.findParentClasses
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

@Suppress("MemberVisibilityCanBePrivate")
class ArgumentStorage : Cloneable {
    private var namedArgument = mutableMapOf<String, Any?>()

    private var classArgument = mutableMapOf<KClass<out Any>, MutableList<Any>>()

    operator fun get(name: String): Any? {
        return namedArgument[name]
    }

    operator fun <T : Any> get(kls: KClass<T>): T? {
        return classArgument[kls]?.getOrNull(0) as T?
    }

    operator fun <T : Any> get(cls: Class<T>): T? {
        return get(cls.kotlin)
    }

    fun <T: Any> getAll(kls: KClass<T>): List<T> {
        if (classArgument[kls] == null)
            return emptyList()
        return classArgument[kls]!!.toList() as List<T>
    }

    fun <T: Any> getAll(cls: Class<T>): List<T> {
        return getAll(cls.kotlin)
    }

    operator fun set(name: String, arg: Any?): ArgumentStorage {
        namedArgument[name] = arg
        return this
    }

    fun add(arg: Any): ArgumentStorage {
        arg.javaClass.kotlin.findParentClasses().forEach {
            classArgument.getOrPut(it) { mutableListOf() }.add(arg)
        }
        return this
    }

    fun removeKey(name: String): ArgumentStorage {
        namedArgument -= name
        return this
    }

    fun remove(arg: Any): ArgumentStorage {
        arg.javaClass.kotlin.findParentClasses().forEach {
            classArgument[it]?.remove(arg)
        }
        return this
    }

    fun removeAll(kls: KClass<*>): ArgumentStorage {
        val iterator = classArgument.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next.key.isSubclassOf(kls)) {
                iterator.remove()
            }
        }
        return this
    }

    fun removeAll(cls: Class<*>): ArgumentStorage {
        return removeAll(cls.kotlin)
    }

    fun contains(name: String): Boolean {
        return name in namedArgument
    }

    fun contains(kls: KClass<out Any>): Boolean {
        return kls in classArgument
    }

    fun contains(cls: Class<out Any>): Boolean {
        return contains(cls.kotlin)
    }

    fun replace(name: String, arg: Any): Any? {
        return namedArgument.put(name, arg)
    }

    fun clear() {
        namedArgument.clear()
        classArgument.clear()
    }

    fun copy(copyNamedArgs: Boolean, copyClassArgs: Boolean): ArgumentStorage {
        val newStorage = ArgumentStorage()
        if (copyNamedArgs)
            newStorage.namedArgument.putAll(namedArgument)
        else
            newStorage.namedArgument = namedArgument

        if (copyClassArgs)
            classArgument.forEach { (k, v) ->
                newStorage.classArgument[k] = v.toMutableList()
            }
        else
            newStorage.classArgument = classArgument

        return newStorage
    }

    public override fun clone(): ArgumentStorage {
        return copy(copyNamedArgs = true, copyClassArgs = true)
    }

    /**
     * Utility Functions
     */

    inline fun <reified T : Any> find(): T {
        return get(T::class) as T
    }

    inline fun <reified T : Any> findNullable(): T? {
        return get(T::class)
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> findAll(): List<T> {
        return getAll(T::class)
    }

    operator fun plusAssign(arg: Any) {
        add(arg)
    }

    operator fun minusAssign(arg: Any) {
        remove(arg)
    }
}