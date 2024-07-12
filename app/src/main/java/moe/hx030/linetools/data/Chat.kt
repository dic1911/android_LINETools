package moe.hx030.linetools.data

class Chat {
    lateinit var id: String
    lateinit var title: String
    lateinit var last_message: String
    lateinit var last_created_time: String
    var message_count: Long = 0

    constructor(id: String, tt: String, last_msg: String, last_created: String, msg_count: Long) {
        this.id = id;
        title = tt
        last_message = last_msg
        last_created_time = last_created
        message_count = msg_count
    }
}