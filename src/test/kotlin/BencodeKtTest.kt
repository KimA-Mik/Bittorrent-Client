import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class BencodeKtTest {

    @Test
    fun decodeBencode() {
        assertEquals("hello", decodeBencode("5:hello"))
        assertEquals("hello", decodeBencode("5:hello123"))
    }
}