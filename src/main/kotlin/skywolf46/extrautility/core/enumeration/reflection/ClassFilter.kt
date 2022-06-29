package skywolf46.extrautility.core.enumeration.reflection

import skywolf46.extrautility.core.abstraction.JvmFilter
import skywolf46.extrautility.core.util.safeKotlin
import java.lang.reflect.Modifier
import kotlin.reflect.KVisibility

private typealias JvmModifier = Modifier

enum class ClassFilter : JvmFilter<Class<*>> {
    INSTANCE_NOT_REQUIRED {
        override fun isSatisfied(data: Class<*>): Boolean {
            return try {
                val kls = data.kotlin
                kls.isCompanion || kls.objectInstance != null
            } catch (e: Throwable) {
                false
            }
        }
    },

    INSTANCE_REQUIRED {
        override fun isSatisfied(data: Class<*>): Boolean {
            return !INSTANCE_REQUIRED.isSatisfied(data)
        }
    },

    BLUEPRINT {
        override fun isSatisfied(data: Class<*>): Boolean {
            return Modifier.INTERFACE.isSatisfied(data) || Modifier.ABSTRACT.isSatisfied(data)
        }
    }
    ;

    enum class Modifier : JvmFilter<Class<*>> {
        INTERFACE {
            override fun isSatisfied(data: Class<*>): Boolean {
                return JvmModifier.isInterface(data.modifiers)
            }
        },

        ABSTRACT {
            override fun isSatisfied(data: Class<*>): Boolean {
                return JvmModifier.isAbstract(data.modifiers)
            }
        },

        OPEN {
            override fun isSatisfied(data: Class<*>): Boolean {
                return data.safeKotlin()?.isOpen ?: false
            }
        },

        FINAL {
            override fun isSatisfied(data: Class<*>): Boolean {
                return JvmModifier.isStatic(data.modifiers)
            }
        },

        PUBLIC {
            override fun isSatisfied(data: Class<*>): Boolean {
                return JvmModifier.isPublic(data.modifiers)
            }
        },

        PROTECTED {
            override fun isSatisfied(data: Class<*>): Boolean {
                return JvmModifier.isProtected(data.modifiers)
            }
        },

        PRIVATE {
            override fun isSatisfied(data: Class<*>): Boolean {
                return JvmModifier.isPrivate(data.modifiers)
            }
        },

        INTERNAL {
            override fun isSatisfied(data: Class<*>): Boolean {
                return data.safeKotlin()?.visibility == KVisibility.INTERNAL
            }
        }
    }
}