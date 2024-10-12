import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BencodeKtTest {
    private val bencode = Bencode()

    @Test
    fun decodeString() {
        assertEquals("hello", bencode.decodeBencode("5:hello"))
        assertEquals("hello", bencode.decodeBencode("5:hello123"))
    }

    @Test
    fun decodeInteger() {
        assertEquals(52L, bencode.decodeBencode("i52e"))
        assertEquals(-52L, bencode.decodeBencode("i-52e"))
        assertEquals(4294967300L, bencode.decodeBencode("i4294967300e"))
    }

    @Test
    fun decodeLeadingZeroInteger() {
        assertEquals(0L, bencode.decodeBencode("i0e"))
        assertThrows<NumberFormatException> { bencode.decodeBencode("i-0e") }
        assertThrows<NumberFormatException> { bencode.decodeBencode("i03e") }
    }

    @Test
    fun decodeList() {
        val expexted = listOf("hello", 52L)
        assertEquals(expexted, bencode.decodeBencode("l5:helloi52ee"))
    }
}