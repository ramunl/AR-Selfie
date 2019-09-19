package us.cyberstar.domain.internal.loader

import us.cyberstar.domain.external.loader.ArWorldLoaderFabric
import us.cyberstar.domain.external.loader.local.ArWorldLoaderFromFile
import us.cyberstar.domain.external.loader.grpc.entity.ArWorldLoaderGrpc
import us.cyberstar.domain.external.loader.base.ArWorldLoader
import us.cyberstar.domain.internal.ArWorldLoaderSettings
import javax.inject.Inject

class ArWorldLoaderFabricImpl @Inject constructor(
    private val arWorldLoaderSettings: ArWorldLoaderSettings,
    private val arWorldLoaderFromFile: ArWorldLoaderFromFile,
    private val arWorldLoaderGrpc: ArWorldLoaderGrpc
) : ArWorldLoaderFabric {

    override fun getLoader(): ArWorldLoader {
        return if (arWorldLoaderSettings.isLocal) {
            arWorldLoaderGrpc.stopSceneWorldUpdating()
            arWorldLoaderFromFile
        } else {
            arWorldLoaderFromFile.stopSceneWorldUpdating()
            arWorldLoaderGrpc
        }
    }
}