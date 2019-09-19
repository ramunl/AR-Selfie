package us.cyberstar.data.model

data class BackendResponseModel(val requestCommand: String) {
    var isSuccess: Boolean = false
    var token: String? = null
    var secondsToExpire: Int? = null
    var responseMsg: String? = null
    override fun toString(): String {
        return "BackendResponseModel(requestCommand='$requestCommand', isSuccess=$isSuccess, secondsToExpire=$secondsToExpire, responseMsg=$responseMsg)"
    }
}