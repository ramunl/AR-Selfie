package us.cyberstar.domain.external.loader

import us.cyberstar.domain.external.loader.base.ArWorldLoader

interface ArWorldLoaderFabric {
    fun getLoader(): ArWorldLoader
}