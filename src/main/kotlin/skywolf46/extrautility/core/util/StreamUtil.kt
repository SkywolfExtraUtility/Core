package skywolf46.extrautility.core.util

import java.io.*

object StreamUtil {

    fun newByteInput(arr: ByteArray): ByteDataInput {
        return ByteDataInput(arr)
    }

    fun newByteOutput(): ByteDataOutput {
        return ByteDataOutput(ByteArrayOutputStream())
    }

    class ByteDataInput private constructor(private val handle: DataInputStream) : DataInput by handle,
        Closeable by handle {
        constructor(arr: ByteArray) : this(DataInputStream(ByteArrayInputStream(arr)))
    }

    class ByteDataOutput(
        private val array: ByteArrayOutputStream,
        private val output: DataOutputStream = DataOutputStream(array)
    ) : DataOutput by output, Closeable by output {
        fun get(): ByteArray {
            return array.toByteArray()
        }

        fun getAndClose(): ByteArray {
            return get().apply {
                close()
            }
        }
    }
}