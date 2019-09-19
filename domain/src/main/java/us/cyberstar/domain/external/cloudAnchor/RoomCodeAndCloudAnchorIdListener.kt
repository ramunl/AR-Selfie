package us.cyberstar.domain.external.cloudAnchor

import com.google.ar.core.Anchor

interface RoomCodeAndCloudAnchorIdListener {
    interface AnchorAndShareListener {
        fun onAnchorShared(roomId: Long)
    }
    interface AnchorLoadListener {
        fun onAnchorLoaded(anchorId: String)
    }

    fun hostAnchorAndShare(anchor: Anchor, anchorAndShareListener: AnchorLoadListener)
}
