package us.cyberstar.domain.internal.model

import android.util.SizeF
import com.google.ar.sceneform.math.Vector3

val cellMargin: Float = 0.05f
val cellDepth: Float = 0.1f
val cellSizeStep: Float = 0.5f
val cellSizeSmall: Vector3 = Vector3(cellSizeStep, cellSizeStep, cellDepth)
val cellSizeBig: Vector3 = Vector3(cellSizeStep * 2f, cellSizeStep * 2f, cellDepth)
val cellSizeRectangle: SizeF = SizeF(cellSizeStep * 2f, cellSizeStep * 4f)
val cellSizeRectangleLarge: SizeF = SizeF(cellSizeStep * 4f, cellSizeStep * 8f)