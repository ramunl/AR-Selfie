package us.cyberstar.data.mapper

import social.Social
import us.cyberstar.data.entity.PostCompositeIdEntity


fun mapToPostCompositeId(postCompositeIdEntity: PostCompositeIdEntity) =
    with(postCompositeIdEntity) {
        Social.PostCompositeID.newBuilder()
            .setPostId(postCompositeIdEntity.postId)
            .build()
    }

fun mapToPostCompositeIdEntity(postCompositeID: Social.PostCompositeID): PostCompositeIdEntity {
    return with(postCompositeID) {
        PostCompositeIdEntity(serverId, wallId, postId)
    }
}