package torrent

import bencode.DecodingResult

data class TorrentInfo(
    //size of the file in bytes, for single-file torrents
    val length: Long,
    //suggested name to save the file / directory as
    val name: String,
) {
    companion object {
        fun extract(dict: Map<String, DecodingResult>): TorrentInfo? {
            val lengthResult = dict[LENGTH_KEY]
            if (lengthResult !is DecodingResult.NumberResult) {
                return null
            }

            val nameResult = dict[NAME_KEY]
            if (nameResult !is DecodingResult.StringResult) {
                return null
            }

            return TorrentInfo(
                length = lengthResult.value,
                name = nameResult.value,
            )
        }

        private const val LENGTH_KEY = "length"
        private const val NAME_KEY = "name"
    }
}