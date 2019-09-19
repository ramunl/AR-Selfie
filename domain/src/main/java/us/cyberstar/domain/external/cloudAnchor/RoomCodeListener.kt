package us.cyberstar.domain.external.cloudAnchor

import com.google.firebase.database.DatabaseError

/** Listener for a new room code.  */
interface RoomCodeListener {

    /** Invoked when a new room code is available from Firebase.  */
    fun onNewRoomCode(newRoomCode: Long?)

    /** Invoked if a Firebase Database Error happened while fetching the room code.  */
    fun onError(error: DatabaseError?)
}