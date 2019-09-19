/*
 * Copyright 2018 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.cyberstar.domain.internal.cloudAnchor

import com.google.ar.core.Anchor
import com.google.ar.core.Anchor.CloudAnchorState
import com.google.ar.core.Session
import com.google.common.base.Preconditions
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.domain.external.arcore.ArCoreSession
import us.cyberstar.domain.external.cloudAnchor.CloudAnchorManager
import java.util.HashMap
import javax.inject.Inject

/**
 * A helper class to handle all the Cloud Anchors logic, and add a callback-like mechanism on top of
 * the existing ARCore API.
 */
internal class CloudAnchorManagerImpl @Inject constructor(
    arCoreSession: ArCoreSession
) : CloudAnchorManager {

    val session = arCoreSession.session
    var pendingAnchors = HashMap<Anchor, CloudAnchorManager.CloudAnchorListener>()

    /**
     * This method hosts an anchor. The `listener` will be invoked when the results are
     * available.
     */
    @Synchronized
    override fun hostCloudAnchor(anchor: Anchor, listener: CloudAnchorManager.CloudAnchorListener) {
        val newAnchor = session.hostCloudAnchor(anchor)
        pendingAnchors[newAnchor] = listener
    }

    /**
     * This method resolves an anchor. The `listener` will be invoked when the results are
     * available.
     */
    @Synchronized
    override fun resolveCloudAnchor(anchorId: String, listener: CloudAnchorManager.CloudAnchorListener) {
        Preconditions.checkNotNull(session, "The session cannot be null.")
        val newAnchor = session.resolveCloudAnchor(anchorId)
        pendingAnchors[newAnchor] = listener
    }

    /** Should be called after a [Session.update] call.  */
    @Synchronized
    override fun onUpdate() {
        Preconditions.checkNotNull(session, "The session cannot be null.")
        val iter = pendingAnchors.entries.iterator()
        while (iter.hasNext()) {
            val entry = iter.next()
            val anchor = entry.key
            if (isReturnableState(anchor.cloudAnchorState)) {
                val listener = entry.value
                listener.onCloudTaskComplete(anchor)
                iter.remove()
            }
        }
    }

    /** Used to clear any currently registered listeners, so they wont be called again.  */
    @Synchronized
    fun clearListeners() {
        pendingAnchors.clear()
    }

    companion object {
        private fun isReturnableState(cloudState: CloudAnchorState): Boolean {
            return when (cloudState) {
                CloudAnchorState.NONE, CloudAnchorState.TASK_IN_PROGRESS -> false
                else -> true
            }
        }
    }
}
