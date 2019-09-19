package us.cyberstar.domain.internal.factory

import android.graphics.Bitmap
import android.location.Location
import com.cyber.math.Matrix4
import com.google.ar.core.Camera
import com.google.ar.sceneform.math.Vector3
import timber.log.Timber
import us.cyberstar.common.utils.timeNow
import us.cyberstar.data.entity.*
import us.cyberstar.data.entity.social.ARPosterEntity
import us.cyberstar.data.entity.social.ArPostEntity
import us.cyberstar.data.entity.social.PostContentEntity
import us.cyberstar.data.ext.cameraOrientation


fun createPostEntity(
    isQuick: Boolean?,
    scale: Int,
    postId: Long,
    transformAndr: Matrix4?,
    arPoster: ARPosterEntity,
    title: String,
    location: Location?,
    postContentEntity: PostContentEntity?,
    isTransformable: Boolean,
    anchorId: String?
): ArPostEntity {
    Timber.d("creating ArPostEntity...")
    return ArPostEntity(
        scale,
        isQuick,
        title,
        arPoster,
        PostCompositeIdEntity(0, 0, postId),
        transformAndr,
        location,
        postContentEntity,
        isTransformable,
        anchorId
    )
}

fun createAssetForDetectionEntity(
    hitPoint: Vector3,
    planeParentTransform: Matrix4,
    camToPlaneDistance: Float,
    camera: Camera,
    location: Location,
    widthInPixels: Float,
    heightInPixels: Float,
    //snapshot: String?,
    jpegBytes: Bitmap?
): AssetForDetectionEntity {
    val timeStamp = timeNow()
    Timber.d("creating AssetForDetectionEntity...")
    //val planeParentTransform = planeParentTransform.centerPose.andrMatrix4()
    val focusX = camera.imageIntrinsics.focalLength[0]
    val focusY = camera.imageIntrinsics.focalLength[1]
    Timber.d("focalLength = ($focusX, $focusY)")
    val physicalWidth = camToPlaneDistance * widthInPixels / focusX
    val physicalHeight = camToPlaneDistance * heightInPixels / focusY

    val sessionToAssetTransform = Matrix4().apply {
        values[Matrix4.M11] = planeParentTransform.values[Matrix4.M11]
        values[Matrix4.M12] = planeParentTransform.values[Matrix4.M12]
        values[Matrix4.M13] = planeParentTransform.values[Matrix4.M13]
        values[Matrix4.M14] = hitPoint.x

        values[Matrix4.M21] = planeParentTransform.values[Matrix4.M21]
        values[Matrix4.M22] = planeParentTransform.values[Matrix4.M22]
        values[Matrix4.M23] = planeParentTransform.values[Matrix4.M23]
        values[Matrix4.M24] = hitPoint.y

        values[Matrix4.M31] = planeParentTransform.values[Matrix4.M31]
        values[Matrix4.M32] = planeParentTransform.values[Matrix4.M32]
        values[Matrix4.M33] = planeParentTransform.values[Matrix4.M33]
        values[Matrix4.M34] = hitPoint.z

        values[Matrix4.M41] = 0f
        values[Matrix4.M42] = 0f
        values[Matrix4.M43] = 0f
        values[Matrix4.M44] = 1f
    }


    return AssetForDetectionEntity(
        camera.cameraOrientation(),
        sessionToAssetTransform,
        physicalHeight,
        physicalWidth,
        location,
        timeStamp,
        //snapshot,
        jpegBytes
    )
}

