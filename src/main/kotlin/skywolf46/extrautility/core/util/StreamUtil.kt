package skywolf46.extrautility.core.util

import java.io.*

object StreamUtil {

    fun newByteInput(arr: ByteArray): ByteDataInput {
        return ByteDataInput(arr)
    }

    fun newByteOutput(): ByteDataOutput {
        return ByteDataOutput()
    }

    class ByteDataInput(arr: ByteArray) : DataInput, Closeable {
        private val rawStream = ByteArrayInputStream(arr)
        private val wrappedStream = DataInputStream(rawStream)

        override fun readFully(b: ByteArray) {
            wrappedStream.readFully(b)
        }

        override fun readFully(b: ByteArray, off: Int, len: Int) {
            wrappedStream.readFully(b, off, len)
        }

        override fun skipBytes(n: Int): Int {
            return wrappedStream.skipBytes(n)
        }

        override fun readBoolean(): Boolean {
            return wrappedStream.readBoolean()
        }

        override fun readByte(): Byte {
            return wrappedStream.readByte()
        }

        override fun readUnsignedByte(): Int {
            return wrappedStream.readUnsignedByte()
        }

        override fun readShort(): Short {
            return wrappedStream.readShort()
        }

        override fun readUnsignedShort(): Int {
            return wrappedStream.readUnsignedShort()
        }

        override fun readChar(): Char {
            return wrappedStream.readChar()
        }

        override fun readInt(): Int {
            return wrappedStream.readInt()
        }

        override fun readLong(): Long {
            return wrappedStream.readLong()
        }

        override fun readFloat(): Float {
            return wrappedStream.readFloat()
        }

        override fun readDouble(): Double {
            return wrappedStream.readDouble()
        }

        override fun readLine(): String {
            return wrappedStream.readLine()
        }

        override fun readUTF(): String {
            return wrappedStream.readUTF()
        }

        override fun close() {
            wrappedStream.close()
        }

    }

    class ByteDataOutput : DataOutput, Closeable {
        private val rawStream = ByteArrayOutputStream()
        private val wrappedStream = DataOutputStream(rawStream)

        override fun write(b: Int) {
            wrappedStream.write(b)
        }

        override fun write(b: ByteArray) {
            wrappedStream.write(b)
        }

        override fun write(b: ByteArray, off: Int, len: Int) {
            wrappedStream.write(b, off, len)
        }

        override fun writeBoolean(v: Boolean) {
            wrappedStream.writeBoolean(v)
        }

        override fun writeByte(v: Int) {
            wrappedStream.writeByte(v)
        }

        override fun writeShort(v: Int) {
            wrappedStream.writeShort(v)
        }

        override fun writeChar(v: Int) {
            wrappedStream.writeChar(v)
        }

        override fun writeInt(v: Int) {
            wrappedStream.writeInt(v)
        }

        override fun writeLong(v: Long) {
            wrappedStream.writeLong(v)
        }

        override fun writeFloat(v: Float) {
            wrappedStream.writeFloat(v)
        }

        override fun writeDouble(v: Double) {
            wrappedStream.writeDouble(v)
        }

        override fun writeBytes(s: String) {
            wrappedStream.writeBytes(s)
        }

        override fun writeChars(s: String) {
            wrappedStream.writeChars(s)
        }

        override fun writeUTF(s: String) {
            wrappedStream.writeUTF(s)
        }

        override fun close() {
            rawStream.close()
        }

        fun get(): ByteArray {
            return rawStream.toByteArray()
        }

        fun getAndClose(): ByteArray {
            return get().apply {
                close()
            }
        }
    }
}