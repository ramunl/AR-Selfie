package us.cyberstar.domain.external.cloudAnchor

interface FirebaseManager {
    fun storeAnchorIdInRoom(roomCode: Long, cloudAnchorId: String)
    fun getNewRoomCode(listener: RoomCodeListener)
    fun registerNewListenerForRoom(roomCode: Long?, listener: CloudAnchorIdListener)
}