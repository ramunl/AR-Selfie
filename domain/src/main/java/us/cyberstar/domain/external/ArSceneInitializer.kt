package us.cyberstar.domain.external

interface ArSceneInitializer {
    fun onDestroy()
    fun startTelemetry(withVideo: Boolean)
    fun stopTelemetry()
    fun toggleLocalRemote(isLocal: Boolean)
    fun initScene()
    fun loadWorld(isRunning: Boolean)
    fun createHorGrid()
    fun destroyHorGrid()
}