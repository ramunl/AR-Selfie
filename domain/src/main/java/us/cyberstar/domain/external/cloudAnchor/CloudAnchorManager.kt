package us.cyberstar.domain.external.cloudAnchor

import com.google.ar.core.Anchor
import us.cyberstar.domain.internal.cloudAnchor.CloudAnchorManagerImpl

interface CloudAnchorManager {
    fun hostCloudAnchor(anchor: Anchor, listener: CloudAnchorListener)
    /** Listener for the results of a host or resolve operation.  */
     interface CloudAnchorListener {
        /** This method is invoked when the results of a Cloud Anchor operation are available.  */
        fun onCloudTaskComplete(anchor: Anchor)
    }
    fun onUpdate()
    fun resolveCloudAnchor(anchorId: String, listener: CloudAnchorListener)
}