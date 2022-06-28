package skywolf46.extrautility.core.test.suites.stream

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import skywolf46.extrautility.core.util.StreamUtil

@TestMethodOrder(OrderAnnotation::class)
class DataStreamTest {
    companion object {
        private val output = StreamUtil.newByteOutput()
        private lateinit var input: StreamUtil.ByteDataInput
    }

    @Test
    @Order(0)
    fun testDataWrite() {
        // Write empty 4 byte for skip
        output.write(ByteArray(4))
        // Write 11
        output.write(11)
        // Write 13
        output.write(13)
        // Write [94, 11, 20]
        output.write(byteArrayOf(94, 11, 20))
        // Write 94
        output.write(byteArrayOf(94, 11, 20), 0, 1)
        // Write true
        output.writeBoolean(true)
        // Write 41
        output.writeByte(41)
        // Write 1898
        output.writeShort(1898)
        // Write 2717
        output.writeShort(2717)
        // Write 'A'
        output.writeChar('A'.toInt())
        // Write 88611
        output.writeInt(88611)
        // Write 9712890
        output.writeLong(9712890)
        // Write 28295.1f
        output.writeFloat(28295.1f)
        // Write 9812863.0
        output.writeDouble(9812863.0)
        // Write "Test" as byte
        output.writeBytes("Test")
        // Write "Test2" as chars
        output.writeChars("Test2")
        // Write "Test3" as UTF
        output.writeUTF("Test3")
        // Write "Lorem\nIpsum"
        output.writeBytes("Lorem\nIpsum")
        // Close and make ByteDataInput
        input = StreamUtil.newByteInput(output.getAndClose())
    }

    @Test
    @Order(1)
    fun testDataRead() {
        input.skipBytes(4)
        assertEquals(11, input.readByte())
        assertEquals(13, input.readUnsignedByte())
        assertArrayEquals(byteArrayOf(94, 11, 20), ByteArray(3).apply {
            input.readFully(this)
        })
        assertArrayEquals(byteArrayOf(94, 0, 0), ByteArray(3).apply {
            input.readFully(this, 0, 1)
        })
        assertTrue(input.readBoolean())
        assertEquals(41, input.readByte())
        assertEquals(1898, input.readShort())
        assertEquals(2717, input.readUnsignedShort())
        assertEquals('A', input.readChar())
        assertEquals(88611, input.readInt())
        assertEquals(9712890, input.readLong())
        assertEquals(28295.1f, input.readFloat())
        assertEquals(9812863.0, input.readDouble())
        assertEquals("Test", String(ByteArray(4).apply {
            input.readFully(this)
        }))
        assertEquals("Test2", String(CharArray(5) { input.readChar() }))
        assertEquals("Test3", input.readUTF())
        assertEquals("Lorem", input.readLine())
        assertEquals("Ipsum", input.readLine())
        input.close()
    }


}