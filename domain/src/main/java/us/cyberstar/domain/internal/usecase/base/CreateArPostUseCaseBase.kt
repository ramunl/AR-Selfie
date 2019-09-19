package us.cyberstar.domain.internal.usecase.base

import android.content.Context
import android.graphics.Bitmap
import android.view.MotionEvent
import com.cyber.math.Matrix4
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.external.sensor.GPSCoordinatesListener
import us.cyberstar.data.external.s3.S3Cache
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.loader.CreatePostFabric
import us.cyberstar.domain.external.loader.grpc.telemetry.AssetForDetectionEmitter
import us.cyberstar.domain.external.loader.grpc.telemetry.PostEntityEmitter
import us.cyberstar.domain.external.loader.grpc.telemetry.OnUpdateListener


/**
 * This class produces PostReqEntity object. The consumers are Local saver/Grpc sender
 */
abstract class CreateArPostUseCaseBase(
    protected open val compositeDisposable: CompositeDisposable,
    protected open val arCoreFrameEmitter: ArCoreFrameEmitter,
    protected open val context: Context,
    protected open val s3Cache: S3Cache,
    protected open val snackBarProvider: SnackBarProvider,
    protected open val postEntityEmitter: PostEntityEmitter,
    protected open val assetForDetectionEmitter: AssetForDetectionEmitter,
    protected open val createPostFabric: CreatePostFabric,
    protected open val gpsCoordinatesListener: GPSCoordinatesListener
) : OnUpdateListener {


    fun subscribeToArFrames() {
        Timber.d("CreateArPostUseCaseBase subscribed to Ar posts")
        arCoreFrameEmitter.addUpdateListener(this)
    }

    fun doCreateArPost(
        scale: Int,
        isQuick: Boolean,
        motionEvent: MotionEvent,
        description: String?,
        photoBitmap: Bitmap?,
        transform: Matrix4 //in debug mode we take hit result plane when we tap on screen
    ) {
        /*doCreateArPost(
            scale,
            isQuick,
            Point(motionEvent.x.toInt(), motionEvent.y.toInt()),
            title,
            mediaPath,
            transform
        )*/
    }


}