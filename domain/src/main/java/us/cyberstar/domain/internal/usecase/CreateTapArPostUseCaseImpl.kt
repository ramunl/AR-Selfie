package us.cyberstar.domain.internal.usecase

import android.content.Context
import android.location.Location
import android.view.MotionEvent
import com.google.ar.core.*
import com.google.ar.sceneform.FrameTime
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.external.sensor.GPSCoordinatesListener
import us.cyberstar.data.external.s3.S3Cache
import us.cyberstar.data.entity.AssetForDetectionEntity
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.loader.grpc.telemetry.AssetForDetectionEmitter
import us.cyberstar.domain.external.loader.grpc.telemetry.PostEntityEmitter
import us.cyberstar.domain.external.helper.TapHelper
import us.cyberstar.domain.external.loader.CreatePostFabric
import us.cyberstar.domain.external.usecase.CreateTapArPostUseCase
import us.cyberstar.domain.gl.common.rendering.PlaneRenderer
import us.cyberstar.domain.internal.utils.*
import javax.inject.Inject


/**
 * This class
 * 1) implements TAP gesture listener,
 * 2) detects nearest tapped plane and then:
 * 3) emits AssetForDetectionEntity, ArPostEntity, CreatePostRequestEntity
 */
internal class CreateTapArPostUseCaseImpl @Inject constructor(
    override val tapHelper: TapHelper,
    override val compositeDisposable: CompositeDisposable,
    override val arCoreFrameEmitter: ArCoreFrameEmitter,
    override val context: Context,
    override val s3Cache: S3Cache,
    override val snackBarProvider: SnackBarProvider,
    override val postEntityEmitter: PostEntityEmitter,
    override val assetForDetectionEmitter: AssetForDetectionEmitter,
    override val createPostFabric: CreatePostFabric,
    override val gpsCoordinatesListener: GPSCoordinatesListener
) : CreateTapArPostUseCase(
    tapHelper,
    context,
    s3Cache,
    snackBarProvider,
    postEntityEmitter,
    assetForDetectionEmitter,
    createPostFabric,
    gpsCoordinatesListener,
    compositeDisposable,
    arCoreFrameEmitter
) {
    override fun unsubscribeFromArCoreFrames() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun subscribeToArCoreFrames() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUpdate(frameTime: FrameTime) {
        // Handle only one tap per lastFrame, as taps are usually low frequency compared to lastFrame rate.
        val tap = tapHelper.poll()
        if (tap != null) {
            // handleTap(frame, tap)
        }
    }

    override fun handleTap(frame: Frame, tap: MotionEvent) {
        if (frame.camera.trackingState == TrackingState.TRACKING) {
            val metrics = context.resources.displayMetrics
            val width = metrics.widthPixels
            val height = metrics.heightPixels
            for (hit in frame.hitTest(width / 2f, height / 2f)) {
                // Check if any plane was hit, and if it was hit inside the plane polygon
                val parentPlane = hit.trackable
                // Creates an anchor if a plane or an oriented point was hit.
                val distance =
                    PlaneRenderer.calculateDistanceToPlane(hit.hitPose, frame.camera.pose)
                if ((parentPlane is Plane && parentPlane.isPoseInPolygon(hit.hitPose) && distance > 0)) {
                    gpsCoordinatesListener.getLastKnownLocation()?.let {
                        onLocationReady(it, hit, parentPlane, frame, distance)
                        //createPostReqEntityEmitter.createAssetForDetectionEntity(arPostEntity, null)
                    } ?: snackBarProvider.showError("GPS coordinate not found", false)
                    break;
                }
            }
        }
    }

    private fun onLocationReady(
        location: Location,
        hit: HitResult,
        parentPlane: Plane,
        frame: Frame,
        distance: Float
    ) {

        val bannerTransform = parentPlane.centerPose
        val quatPost = calculateQuartToRotateInParallel(bannerTransform)
        val posPost = hit.hitPose.translation

        Timber.d("onLocationReady quatPost $quatPost")
        val transformRotated =
            transQuatToMatrix2(
                posPost,
                quatPost.asArray()
            )//Pose( posPost, quatPost.asArray()).andrMatrix()
        val scale = 1
        /*val postEntity = postEntityEmitter.createAssetForDetectionEntity(
            scale,
            true,
            hit.hashCode().toLong(),
            transformRotated,
            ArPosterModel(listOf("")),
            "test ${hit.hashCode().toLong()}",
            location
        )*/

        fun onCreated(assetForDetectionEntity: AssetForDetectionEntity) { //TODO find out another solution!
            //   createPostReqEntityEmitter.createAssetForDetectionEntity(postEntity, assetForDetectionEntity)
        }
        /*assetForDetectionEmitter.createAssetForDetectionEntity(
            posPost,
            lastFrame,
            distanceToCamera,
            Matrix4(parentPlane.centerPose.andrMatrix4()),
            ::onCreated
        )*/
    }
}
