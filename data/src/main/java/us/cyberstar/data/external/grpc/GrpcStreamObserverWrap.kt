package us.cyberstar.data.external.grpc


interface GrpcStreamObserverWrap {
    fun onNetworkError(t: Throwable?)
    fun onSuccess(msg: String)
    fun onComplete()
}