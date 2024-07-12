package moe.hx030.linetools.data

class ChatHistory {
    var id: Long = 0
    lateinit var server_id: String
    var type: Int = 0
    lateinit var chat_id: String
    var from_mid: String? = null
    var content: String? = null
    lateinit var created_time: String
    var delivered_time: String? = null
    var status: Int = 0
    var sent_count: Int = 0
    var read_count: Int = 0
    var location_name: String? = null
    var location_address: String? = null
    var location_phone: String? = null
    var location_latitude: Double? = null
    var location_longitude: Double? = null
    var attachement_image: Int? = null
    var attachement_image_height: Long? = null
    var attachement_image_width: Long? = null
    var attachement_image_size: Int? = null
    var attachement_type: Int = 0
    lateinit var attachement_local_uri: String
    var parameter: String? = null
    var chunks: ByteArray? = null
}