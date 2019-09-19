package us.cyberstar.data.entity

data class PostCompositeIdEntity(
    var serverId: Long,
    var wallId: Long,
    var postId: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PostCompositeIdEntity

        if (serverId != other.serverId) return false
        if (wallId != other.wallId) return false
        if (postId != other.postId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = serverId.hashCode()
        result = 31 * result + wallId.hashCode()
        result = 31 * result + postId.hashCode()
        return result
    }
}