package bencode

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import java.io.File

class BencoderTest {
    private val bencoder = Bencoder()
    private val decoder = Bencode()

    @Test
    fun bencode() {
        var bencoded = "5:hello"
        val decoded = decoder.decodeBencode(bencoded)
        assertArrayEquals(bencoded.encodeToByteArray(), bencoder.bencode(decoded))
    }


    @Test
    fun `sample torrent file`() {
        val bytes = File("sample.torrent").readBytes()
        val decoded = decoder.decodeBencode(bytes)

        val result = bencoder.bencode(decoded)
        assertArrayEquals(
            bytes,
            result
        )
    }
}