import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

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
    }
}