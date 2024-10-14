import com.google.gson.Gson
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

    println("Tracker URL: ${torrent.trackerUrl}")
    println("Length: ${torrent.info.length}")
}