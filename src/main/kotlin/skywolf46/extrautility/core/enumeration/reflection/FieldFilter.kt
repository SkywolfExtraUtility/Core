package skywolf46.extrautility.core.enumeration.reflection

import skywolf46.extrautility.core.abstraction.JvmFilter
import skywolf46.extrautility.core.definition.JvmModifier
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter

enum class FieldFilter : JvmFilter<KProperty<*>> {
    INSTANCE_NOT_REQUIRED {
        override fun isSatisfied(data: KProperty<*>): Boolean {
            val member = (data.javaField ?: data.javaGetter)

            if (member != null) {
                if (JvmModifier.isStatic(member.modifiers)) {
                    return true
                }
                return ClassFilter.INSTANCE_NOT_REQUIRED.isSatisfied(member.declaringClass.kotlin)
            }
            return false
        }
    },
    INSTANCE_REQUIRED {
        override fun isSatisfied(data: KProperty<*>): Boolean {
            return !INSTANCE_REQUIRED.isSatisfied(data)
        }
    };

    enum class Modifier : JvmFilter<KProperty<*>> {
        OPEN {
            override fun isSatisfied(data: KProperty<*>): Boolean {
                return data.isOpen
            }
        },
        FINAL {
            override fun isSatisfied(data: KProperty<*>): Boolean {
                return data.isFinal
            }
        },
        PUBLIC {
            override fun isSatisfied(data: KProperty<*>): Boolean {
                return data.visibility == KVisibility.PUBLIC
            }
        },
        PROTECTED {
            override fun isSatisfied(data: KProperty<*>): Boolean {
                return data.visibility == KVisibility.PROTECTED
            }
        },
        PRIVATE {
            override fun isSatisfied(data: KProperty<*>): Boolean {
                return data.visibility == KVisibility.PRIVATE
            }
        },
        INTERNAL {
            override fun isSatisfied(data: KProperty<*>): Boolean {
                return data.visibility == KVisibility.INTERNAL
            }
        },

        STATIC {
            override fun isSatisfied(data: KProperty<*>): Boolean {
                return JvmModifier.isStatic((data.javaField ?: data.javaGetter)?.modifiers ?: 0)
            }
        }
    }
}