package us.cyberstar.domain.external.cloudAnchor


/** Listener for a new cloud anchor ID.  */
interface CloudAnchorIdListener {

    /** Invoked when a new cloud anchor ID is available.  */
    fun onNewCloudAnchorId(cloudAnchorId: String)
}