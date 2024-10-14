data class TorrentFile(
    val trackerUrl: String,
    val info: TorrentInfo,
) {
    companion object {
        fun extract(dict: Map<String, DecodingResult>): TorrentFile? {
            val trackerUrlResult = dict[TORRENT_URL_KEY]
            if (trackerUrlResult !is DecodingResult.StringResult) {
                return null
            }

            val infoResult = dict[INFO_KEY]
            if (infoResult !is DecodingResult.DictionaryResult) {
                return null
            }

            val info = TorrentInfo.extract(infoResult.value) ?: return null

            return TorrentFile(
                trackerUrl = trackerUrlResult.value,
                info = info,
            )
        }

        private const val TORRENT_URL_KEY = "announce"
        private const val INFO_KEY = "info"
    }
}

