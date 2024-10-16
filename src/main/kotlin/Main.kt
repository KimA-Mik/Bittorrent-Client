import bencode.Bencode
import bencode.Bencoder
import bencode.DecodingResult
import com.google.gson.Gson
import org.apache.commons.codec.digest.DigestUtils
import torrent.TorrentFile
import torrent.TorrentFile.Companion.INFO_KEY
import java.io.File

fun main(args: Array<String>) {
    if (args.size != 2) {
        System.err.println("Usage: [decode]  <sequence>")
        System.err.println("       [info]  <filepath>")
        return
    }

    when (val command = args[0]) {
        "decode" -> decode(args[1])
        "info" -> info(args[1])
        else -> println("Unknown command $command")
    }
}

fun decode(encoded: String) {
    val bencode = Bencode()
    val gson = Gson()
    val decoded = bencode.decodeBencode(encoded)
    println(gson.toJson(decoded.getValue()))
}

@OptIn(ExperimentalStdlibApi::class)
fun info(filepath: String) {
    val file = File(filepath)
    if (!file.exists()) {
        return
    }

    val bencode = Bencode()
    val decoded = bencode.decodeBencode(file.readBytes())
    if (decoded !is DecodingResult.DictionaryResult) {
        System.err.println("Unsupported file format")
        return
    }

    val torrent = TorrentFile.extract(decoded.value).onNull {
        System.err.println("Unable to extract info")
        return
    }

    val encoder = Bencoder()
    val bencodedInfo = encoder.bencode(decoded.value[INFO_KEY]!!)

    println("Tracker URL: ${torrent.trackerUrl}")
    println("Length: ${torrent.info.length}")
    println("Info Hash: ${DigestUtils.sha1Hex(bencodedInfo)}")
    println("Piece Length: ${torrent.info.pieceLength}")
    println("Piece Hashes: ")
    torrent.info.pieces.forEach {
        println(it.toHexString(HexFormat.Default))
    }
}