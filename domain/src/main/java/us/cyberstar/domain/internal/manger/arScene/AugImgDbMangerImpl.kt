package us.cyberstar.domain.internal.manger.arScene

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Config
import com.google.ar.core.Session
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.common.utils.removeFrom
import us.cyberstar.data.entity.AssetForDetectionEntity
import us.cyberstar.data.entity.LoadWorldReplyEntity
import us.cyberstar.domain.external.arcore.ArCoreSession
import us.cyberstar.domain.external.loader.ArWorldLoaderFabric
import us.cyberstar.domain.external.loader.grpc.telemetry.AssetForDetectionEmitter
import us.cyberstar.domain.external.loader.s3.MediaLoader
import us.cyberstar.domain.external.manger.AugImgDbManger
import us.cyberstar.domain.internal.loader.grpc.telemetry.EntityEmitterBase
import java.io.IOException
import javax.inject.Inject

/**
 * The class manages augmented image data base. it listens to:
 * 1) Scene loader modules(local/grpc)
 * 2) AssetForDetectionEmitter module <<-- not used anymore, we don't need to add it when we create a post
 * Those 2 modules emits augmented images which AugImgDbManger adds to data base
 *
 */
class AugImgDbMangerImpl @Inject constructor(
    private val compositeDisposable: CompositeDisposable,
    arCoreSession: ArCoreSession,
    private val assetForDetectionEmitter: AssetForDetectionEmitter,
    private val snackBarProvider: SnackBarProvider,
    private val context: Context,
    private val arWorldLoaderFabric: ArWorldLoaderFabric, // we consume loaded images from GRPC server
    val schedulersProvider: us.cyberstar.common.external.SchedulersProvider,
    private val mediaLoader: MediaLoader
) : AugImgDbManger {

    val session: Session = arCoreSession.session
    private var augImageDatabase: AugmentedImageDatabase? = AugmentedImageDatabase(session)

    override fun subscribeToAssetForDetection() {
        Timber.d("subscribed to AssetForDetectionEmitters")
        observe(arWorldLoaderFabric.getLoader())
        //observe(assetForDetectionEmitter)
    }


    override fun removeImages() {
        val config = initConfigForSession(session, AugmentedImageDatabase(session))
        augImageDatabase = config.augmentedImageDatabase
        session.configure(config)
    }

    //TODO refactor this
    private fun initConfigForSession(session: Session, db: AugmentedImageDatabase): Config =
        Config(session).apply {
            augmentedImageDatabase = db
            focusMode = Config.FocusMode.AUTO
            planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
            updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
        }

    private var disposible: Disposable? = null

    override fun unsubscribe() {
        disposible?.removeFrom(compositeDisposable)
    }

    private fun <T> observe(entityEmitter: EntityEmitterBase<T>) {
        disposible = entityEmitter.sourceObservable().subscribe(
            {
                if (it is AssetForDetectionEntity) {
                    addImageToDb(it)
                } else if (it is LoadWorldReplyEntity) {
                    for (asset in it.assetForDetectionArray) {
                        addImageToDb(asset)
                    }
                }
            },
            { Timber.e("sourceObservable $it") },
            { Timber.d("sourceObservable onComplete called") })
            .addTo(compositeDisposable) // TODO clean it where it needs to be
    }

    private fun addImageToDb(entity: AssetForDetectionEntity) {
        with(entity) {
            fun doAddToDb(imageId: String, bitmap: Bitmap, width: Float) {
                //schedulersProvider.newThread().scheduleDirect {
                try {
                    Timber.d("addImageToDb onResourceReady width = $width byteCount = ${bitmap.byteCount} imgName = $imageId")
                    val res = augImageDatabase!!.addImage(imageId, bitmap, width)
                    //  val testBitma = loadBlanketImage()
                    //val res = augImageDatabase!!.addImage(imageId, testBitma, testBitma!!.width.toFloat())
                    //snackBarProvider.showMessage("image added to db imgName $res")
                    // Timber.d("image added with res $res")
                    snackBarProvider.showMessage("image added with res $res $imageId")
                    session.configure(initConfigForSession(session, augImageDatabase!!))
                } catch (e: Exception) {
                    snackBarProvider.showError(e.toString(), false)
                }
                //}
            }


            fun onBitmapLoaded(bitmap: Bitmap?) {
                bitmap?.let {
                    doAddToDb(snapshotS3Path!!, it, physicalWidth)
                }
            }

            snapshotBitmap?.let {
                //if snapshotBitmap != null, it means that user just has created AssetForDetectionEntity
                //snapshotLocalPath must be either S3 path or local storage path
                doAddToDb(snapshotS3Path!!, it, physicalWidth)
            } ?:
            //snapshotBitmap = null means that we loaded this asset either from server or local storage
            //snapshotLocalPath must be initialized with local or s3 path
            snapshotS3Path!!.let { mediaLoader.downLoadImageAsynch(it, ::onBitmapLoaded) }
        }
    }

    private fun loadBlanketImage(): Bitmap? { //TODO For debug!
        try {
            Timber.d("load blanket Image")
            context.assets.open("blanket.jpeg")
                .use { `is` -> return BitmapFactory.decodeStream(`is`) }
        } catch (e: IOException) {
            Timber.e(e)
        }
        return null
    }


}