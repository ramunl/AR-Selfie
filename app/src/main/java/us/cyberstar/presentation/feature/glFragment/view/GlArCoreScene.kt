package us.cyberstar.presentation.feature.glFragment.view

import com.google.ar.core.Anchor
import java.util.*

class GlArCoreScene {
    private val anchors = ArrayList<ColoredAnchor>()

    // Anchors created from taps used for object placing with a given color.
    private class ColoredAnchor(val anchor: Anchor, val color: FloatArray)

    fun refreshAnchors() {

    }
}