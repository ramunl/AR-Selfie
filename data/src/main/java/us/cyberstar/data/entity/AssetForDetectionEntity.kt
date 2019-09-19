package us.cyberstar.data.entity

import android.graphics.Bitmap
import android.location.Location
import com.cyber.math.Matrix4

data class AssetForDetectionEntity(
    //val pose: Pose,
    val cameraTransform: FloatArray,
    val sessionToAssetTransform: Matrix4,
    //val arSceneCenterHitTest: ArSceneCenterHitTest,
    val physicalHeight: Float,
    val physicalWidth: Float,
    val location: Location,
    val timeStamp: Long,
    val snapshotBitmap: Bitmap? = null,
    var snapshotS3Path: String? = null

)