package skywolf46.extrautility.core.test.data

import skywolf46.extrautility.core.test.annotations.TestClassAnnotation
import skywolf46.extrautility.core.test.annotations.TestFieldAnnotation
import skywolf46.extrautility.core.test.annotations.TestMethodAnnotation

@TestClassAnnotation
@Suppress("RedundantVisibilityModifier")
private object PrivateTestAnnotatedObject {
    @TestFieldAnnotation
    private val privateTestAnnotatedField = 1

    @TestFieldAnnotation
    internal val internalTestAnnotatedField = 2

    @TestFieldAnnotation
    val publicTestAnnotatedField = 3

    @TestMethodAnnotation
    private fun privateTestAnnotatedMethod(): Int {
        return 1
    }

    @TestMethodAnnotation
    internal fun internalTestAnnotatedMethod(): Int {
        return 1
    }

    @TestMethodAnnotation
    fun publicTestAnnotatedMethod(): Int {
        return 3
    }
}