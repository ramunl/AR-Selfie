package us.cyberstar.domain.external.usecase

import android.content.Context
import android.view.MotionEvent
import com.google.ar.core.Frame
import io.reactivex.disposables.CompositeDisposable
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.external.sensor.GPSCoordinatesListener
import us.cyberstar.data.external.s3.S3Cache
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.loader.grpc.telemetry.AssetForDetectionEmitter
import us.cyberstar.domain.external.loader.grpc.telemetry.PostEntityEmitter
import us.cyberstar.domain.external.helper.TapHelper
import us.cyberstar.domain.external.loader.CreatePostFabric
import us.cyberstar.domain.internal.usecase.base.CreateArPostUseCaseBase

abstract class CreateTapArPostUseCase(
    open val tapHelper: TapHelper,
    override val context: Context,
    override val s3Cache: S3Cache,
    override val snackBarProvider: SnackBarProvider,
    override val postEntityEmitter: PostEntityEmitter,
    override val assetForDetectionEmitter: AssetForDetectionEmitter,
    override val createPostFabric: CreatePostFabric,
    override val gpsCoordinatesListener: GPSCoordinatesListener,
    compositeDisposable: CompositeDisposable,
    arCoreFrameEmitterBase: ArCoreFrameEmitter
) : CreateArPostUseCaseBase(
    compositeDisposable,
    arCoreFrameEmitterBase,
    context,
    s3Cache,
    snackBarProvider,
    postEntityEmitter,
    assetForDetectionEmitter,
    createPostFabric,
    gpsCoordinatesListener
) {
    abstract fun handleTap(frame: Frame, tap: MotionEvent)
}
