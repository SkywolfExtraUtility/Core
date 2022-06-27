package skywolf46.extrautility.core.enumeration.reflection

import skywolf46.extrautility.core.abstraction.JvmFilter
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility

private typealias JvmModifier = Modifier

enum class ClassFilter : JvmFilter<KClass<*>> {
    INSTANCE_NOT_REQUIRED {
        override fun isSatisfied(data: KClass<*>): Boolean {
            return data.isCompanion || data.objectInstance != null
        }
    },
    INSTANCE_REQUIRED {
        override fun isSatisfied(data: KClass<*>): Boolean {
            return !INSTANCE_REQUIRED.isSatisfied(data)
        }
    },
    ;

    enum class Modifier : JvmFilter<KClass<*>> {
        OPEN {
            override fun isSatisfied(data: KClass<*>): Boolean {
                return data.isOpen
            }
        },
        FINAL {
            override fun isSatisfied(data: KClass<*>): Boolean {
                return data.isFinal
            }
        },
        PUBLIC {
            override fun isSatisfied(data: KClass<*>): Boolean {
                return data.visibility == KVisibility.PUBLIC
            }
        },
        PROTECTED {
            override fun isSatisfied(data: KClass<*>): Boolean {
                return data.visibility == KVisibility.PROTECTED
            }
        },
        PRIVATE {
            override fun isSatisfied(data: KClass<*>): Boolean {
                return data.visibility == KVisibility.PRIVATE
            }
        },
        INTERNAL {
            override fun isSatisfied(data: KClass<*>): Boolean {
                return data.visibility == KVisibility.INTERNAL
            }
        }
    }
}