package us.cyberstar.presentation.feature.scenes.mainScene.provider

import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.view.PixelCopy
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import us.cyberstar.data.external.s3.CacheFileType
import us.cyberstar.data.external.s3.S3Cache
import us.cyberstar.domain.external.model.ArPostModel
import us.cyberstar.domain.external.model.ArPostPhotoModel
import us.cyberstar.presentation.feature.arFragment.view.ArFragmentImpl
import us.cyberstar.presentation.feature.scenes.mainScene.arcore.scene.ArCoreSceneView
import us.cyberstar.presentation.helpers.getPhotoThumbsMap
import javax.inject.Inject

class PhotoContentMakerImpl @Inject constructor(
    private val arCoreSceneView: ArCoreSceneView,
    private val s3Cache: S3Cache,
    private val schedulersProvider: us.cyberstar.common.external.SchedulersProvider
) : PhotoContentMaker {



    //override val photoEmitter: BehaviorSubject<Bitmap> by lazy { BehaviorSubject.create<Bitmap>() }

    //override var lastBitmap: Bitmap? = null

    override fun makePhoto(): Single<ArPostPhotoModel> {
        return Single.create {
            Timber.d("making photo(surfaceView snapshot)")
            val surfaceView = arCoreSceneView.arSceneView
            val w = surfaceView.width
            val h = surfaceView.height
            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val handlerThread = HandlerThread("PixelCopier")
            handlerThread.start()
            // Make the request to copy.
            PixelCopy.request(surfaceView, bitmap, { copyResult: Int ->
                if (copyResult == PixelCopy.SUCCESS) {
                    val path = s3Cache.saveTempFile(bitmap, CacheFileType.SNAPSHOT)
                    it.onSuccess(
                        ArPostPhotoModel(
                            w, h, path,
                            getPhotoThumbsMap(path, s3Cache)
                        )
                    )
                } else {
                    it.onError(Throwable(copyResult.toString()))
                }
                handlerThread.quitSafely()
                handlerThread.interrupt()
            }, Handler(handlerThread.looper))
            Timber.d("..and emit a new bitmap..")
        }
    }
}