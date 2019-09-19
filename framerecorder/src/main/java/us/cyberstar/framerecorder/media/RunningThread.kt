package us.cyberstar.framerecorder.media

open class RunningThread : Thread() {
    var isRunning: Boolean = false

    open fun stopRunning() {
        this.isRunning = false
    }
}