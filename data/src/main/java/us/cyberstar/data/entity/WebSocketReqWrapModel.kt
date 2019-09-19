package us.cyberstar.data.entity

data class WebSocketReqWrapModel(val requestMethod: String,
                                 val params: HashMap<String, String>,
                                 val needAuth: Boolean)