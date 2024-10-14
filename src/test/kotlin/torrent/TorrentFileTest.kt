package torrent

import bencode.Bencode
import bencode.DecodingResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
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
    }
}