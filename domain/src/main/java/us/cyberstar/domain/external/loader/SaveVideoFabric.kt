package us.cyberstar.domain.external.loader

import us.cyberstar.domain.external.loader.base.SaveVideoBase

interface SaveVideoFabric {
    fun getSaveVideoFabric(): SaveVideoBase
}