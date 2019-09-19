package us.cyberstar.domain.internal.loader.local.entity

import us.cyberstar.data.entity.telemetry.*
import us.cyberstar.domain.external.loader.local.SaveVideoLocal

/**
 * The class saves session data ( post, plane, assets)
 * to local storage
 */
internal class SaveVideoLocalImpl : SaveVideoLocal {

    override fun saveVideoRequestEntity(entity: SaveVideoRequestEntity): Boolean {
        //nothing todo here ?
        return true
    }
}