package us.cyberstar.data.entity

import us.cyberstar.data.entity.social.ArPostEntity


data class LoadWorldReplyEntity(
    val sessionId: String,
    val sessionLastUpdatedTimestamp: Double,
    val assetForDetectionArray: Collection<AssetForDetectionEntity>,
    val arPostEntityArray: Collection<ArPostEntity>
)