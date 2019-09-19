package us.cyberstar.domain.external.dictionary

import us.cyberstar.data.entity.AssetForDetectionEntity

interface AssetForDetectionHashMapWrap {

    fun subscribeToAssetForDetections()
    fun getEntity(snapshot: String): AssetForDetectionEntity?
    fun clearAll()

}
