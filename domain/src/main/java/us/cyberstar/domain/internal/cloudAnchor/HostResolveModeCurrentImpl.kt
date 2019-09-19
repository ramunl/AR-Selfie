package us.cyberstar.domain.internal.cloudAnchor

import us.cyberstar.domain.external.cloudAnchor.HostResolveMode
import us.cyberstar.domain.external.cloudAnchor.HostResolveModeCurrent

class HostResolveModeCurrentImpl: HostResolveModeCurrent {
    override var mode: HostResolveMode = HostResolveMode.NONE
}