package us.cyberstar.domain.external.loader.base

import us.cyberstar.data.entity.telemetry.*

interface SaveVideoBase  {
    fun saveVideoRequestEntity(entity: SaveVideoRequestEntity): Boolean
}