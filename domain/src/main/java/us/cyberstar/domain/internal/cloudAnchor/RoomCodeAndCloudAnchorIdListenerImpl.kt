package us.cyberstar.domain.internal.cloudAnchor

import com.google.ar.core.Anchor
import com.google.firebase.database.DatabaseError
import timber.log.Timber
import us.cyberstar.common.external.ResRepo
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.domain.R
import us.cyberstar.domain.external.cloudAnchor.*
import javax.inject.Inject

/**
 * Listens for both a new room code and an anchor ID, and shares the anchor ID in Firebase with
 * the room code when both are available.
 */
internal class RoomCodeAndCloudAnchorIdListenerImpl @Inject constructor(
    private val cloudAnchorManager: CloudAnchorManager,
   // private val firebaseManager: FirebaseManager,
    private val resRepo: ResRepo,
    private val snackBarProvider: SnackBarProvider
) : RoomCodeAndCloudAnchorIdListener {
    override fun hostAnchorAndShare(
        anchor: Anchor,
        anchorAndShareListener: RoomCodeAndCloudAnchorIdListener.AnchorLoadListener
    ) {
        cloudAnchorManager.hostCloudAnchor(anchor, object : CloudAnchorManager.CloudAnchorListener {
            override fun onCloudTaskComplete(anchor: Anchor) {
                val cloudState = anchor.cloudAnchorState
                if (!cloudState.isError) {
                    anchorAndShareListener.onAnchorLoaded(anchor.cloudAnchorId)
                    //checkAndShare(anchor.cloudAnchorId, anchorAndShareListener)
                } else {
                    Timber.e("Error hosting a cloud anchor, state $cloudState")
                    snackBarProvider.showMessage("${resRepo.getString(R.string.snackbar_host_error)} $cloudState")
                }
            }
        })
    }

    /*private fun checkAndShare(
        cloudAnchorId: String,
        anchorAndShareListener: RoomCodeAndCloudAnchorIdListener.AnchorAndShareListener
    ) {
        firebaseManager.getNewRoomCode(object : RoomCodeListener {
            override fun onError(error: DatabaseError?) {
                snackBarProvider.showError(error!!.toString(), false)
            }

            override fun onNewRoomCode(newRoomCode: Long?) {
                firebaseManager.storeAnchorIdInRoom(newRoomCode!!, cloudAnchorId)
                anchorAndShareListener.onAnchorShared(newRoomCode)
                snackBarProvider.showMessage(resRepo.getString(R.string.snackbar_cloud_id_shared))
            }
        })
    }*/

}