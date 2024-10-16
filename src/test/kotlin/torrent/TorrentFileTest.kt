package torrent

import bencode.Bencode
import bencode.DecodingResult
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import java.io.File

class TorrentFileTest {
    private val sampleTorrentBytes = File("sample.torrent").readBytes()
    private val bencode = Bencode()


    @Test
    fun `decode sample torrent file`() {
        val decoded = bencode.decodeBencode(sampleTorrentBytes)
        assertInstanceOf<DecodingResult.DictionaryResult>(decoded) { "Torrent file must be decoded a dictionary." }

        val torrentFile = TorrentFile.extract(decoded.value)
        assertNotNull(torrentFile, "Result of decoding must contain all data to construct torrent file structure.")

        assertEquals("http://bittorrent-test-tracker.codecrafters.io/announce", torrentFile!!.trackerUrl)
        assertEquals(92063L, torrentFile.info.length)

        assertEquals(32768, torrentFile.info.pieceLength)


        val pieceHashes = listOf(
            "e876f67a2a8886e8f36b136726c30fa29703022d",
            "6e2275e604a0766656736e81ff10b55204ad8d35",
            "f00d937a0213df1982bc8d097227ad9e909acc17"
        ).map { it.decodeHex() }


        assertEquals(pieceHashes.size, torrentFile.info.pieces.size)
        for (i in pieceHashes.indices) {
            assertArrayEquals(pieceHashes[i], torrentFile.info.pieces[i])
        }
    }

    fun String.decodeHex(): ByteArray {
        require(length % 2 == 0) { "Must have an even length" }

        return chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }
}