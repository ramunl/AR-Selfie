package us.cyberstar.domain.internal.loader.grpc.telemetry

import com.google.ar.core.AugmentedImage
import com.google.ar.sceneform.FrameTime
import io.reactivex.disposables.CompositeDisposable
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.entity.telemetry.DetectedAssetEntity
import us.cyberstar.data.ext.andrMatrix4
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.loader.grpc.telemetry.AugmentedImageEmitter
import javax.inject.Inject


/**
 * The class is responsible for emitting detected Augment images
 */
internal class AugmentedImageEmitterImpl @Inject constructor(
    private val compositeDisposable: CompositeDisposable,
    override val arCoreFrameEmitter: ArCoreFrameEmitter,
    private val snackBarProvider: SnackBarProvider,
    val schedulersProvider: us.cyberstar.common.external.SchedulersProvider
) : AugmentedImageEmitter(arCoreFrameEmitter) {


    override fun onUpdate(frameTime: FrameTime) {
        val lastFrame = arCoreFrameEmitter.lastFrame()
        lastFrame?.let { frame ->
            val augmentedImages = frame.getUpdatedTrackables(AugmentedImage::class.java)
            augmentedImages?.let {
                if (it.isNotEmpty()) {
                    //   snackBarProvider.showMessage("augmentedImages found ${augmentedImages.size}")
                    val arAugmentImgEntities = mutableListOf<DetectedAssetEntity>()
                    for (img in it) {
                        with(img) {
                            arAugmentImgEntities.add(
                                DetectedAssetEntity(
                                    name,
                                    frame.camera.pose.andrMatrix4(),
                                    anchors,
                                    extentX,
                                    extentZ,
                                    centerPose
                                )
                            )
                        }
                    }
                    emitNext(arAugmentImgEntities)
                }
            }
        }
    }
}

