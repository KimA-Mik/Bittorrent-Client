import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BencodeKtTest {

    @Test
    fun decodeString() {
        assertEquals("hello", decodeBencode("5:hello"))
        assertEquals("hello", decodeBencode("5:hello123"))
    }

    @Test
    fun decodeInteger() {
        assertEquals(52L, decodeBencode("i52e"))
        assertEquals(-52L, decodeBencode("i-52e"))
        assertEquals(4294967300L, decodeBencode("i4294967300e"))
    }

    @Test
    fun decodeLeadingZeroInteger() {
        assertEquals(0L, decodeBencode("i0e"))
        assertThrows<NumberFormatException> { decodeBencode("i-0e") }
        assertThrows<NumberFormatException> { decodeBencode("i03e") }
    }
}