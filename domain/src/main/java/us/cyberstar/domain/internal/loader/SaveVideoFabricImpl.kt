package us.cyberstar.domain.internal.loader

import us.cyberstar.domain.external.loader.SaveVideoFabric
import us.cyberstar.domain.external.loader.base.SaveVideoBase
import us.cyberstar.domain.external.loader.grpc.entity.SaveVideoRemote
import us.cyberstar.domain.external.loader.local.SaveVideoLocal
import us.cyberstar.domain.internal.ArWorldLoaderSettings

class SaveVideoFabricImpl(
    private val arWorldLoaderSettings: ArWorldLoaderSettings,
    private val saveVideoLocal: SaveVideoLocal,
    private val saveVideoRemote: SaveVideoRemote
) : SaveVideoFabric {

    override fun getSaveVideoFabric(): SaveVideoBase =
        if (arWorldLoaderSettings.isLocal) saveVideoLocal
        else saveVideoRemote
}