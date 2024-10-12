import com.google.gson.Gson
// import com.dampcake.bencode.Bencode; - available if you need it!

fun main(args: Array<String>) {
    if (args.size != 2) {
        System.err.println("Usage: [decode]  <sequence>")
        return
    }

    when (val command = args[0]) {
        "decode" -> decode(args[1])
        else -> println("Unknown command $command")
    }
}

fun decode(encoded: String) {
    val gson = Gson()
    val decoded = decodeBencode(encoded)
    println(gson.toJson(decoded))
}
