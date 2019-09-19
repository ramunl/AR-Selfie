package us.cyberstar.domain.internal.usecase.base

import android.graphics.Bitmap
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.utils.ImageCopy
import us.cyberstar.common.utils.imageToJPEG
import us.cyberstar.common.utils.jpegToBitmap
import us.cyberstar.common.utils.rotate
import us.cyberstar.data.external.sensor.GPSCoordinatesListener
import us.cyberstar.data.external.s3.S3Cache
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.arcore.ArCoreSession
import us.cyberstar.domain.external.cloudAnchor.RoomCodeAndCloudAnchorIdListener
import us.cyberstar.domain.internal.utils.getPose


/**
 * This class checks all satisfying conditions and makes some preparations to create Ar Post.
 * For targeting post we need:
 * 1) gps coordinate
 * 2) gets last ArFrame
 * 3) search a plane for hit
 * 4) lastFrame image (we need calling lastFrame.acquireCameraImage)
 * for quick post we need:
 * 1) gps coordinate
 */
class PostDataRetrieverImpl(
    val schedulersProvider: SchedulersProvider,
    val arCoreSession: ArCoreSession,
    val s3Cache: S3Cache,
    val arCoreFrameEmitter: ArCoreFrameEmitter,
    val gpsCoordinatesListener: GPSCoordinatesListener,
    val roomCodeAndCloudAnchorIdListener: RoomCodeAndCloudAnchorIdListener
) : PostDataRetriever {


    private val targetingPostDataMap = HashMap<Vector3, TargetingPostData>()

    private fun acquireImage(imageTemp: ImageCopy): Bitmap? {
        val jpegBytes = imageToJPEG(imageTemp)
        val width = imageTemp.width.toFloat()
        val height = imageTemp.height.toFloat()
        return jpegToBitmap(
            jpegBytes,
            width.toInt(), height.toInt()
        )?.rotate(-90f)
    }

    override fun retrieveQuickPostData(): QuickPostData {
        var res: QuickPostData? = null
        Timber.d("retrieveQuickPostData")
        val location = gpsCoordinatesListener.getLastKnownLocation()
        location?.let {
            res = QuickPostData(it)
        } ?: throw Throwable("GPS postLocation has not been determined, try again latter")
        return res!!
    }


    override fun retrieveArModel3dPostData(
        node: Node,
        dataListener: PostDataRetriever.Ar3dModelPostDataListener
    ) {
        Timber.d("retrieveArModel3dPostData")
        roomCodeAndCloudAnchorIdListener.hostAnchorAndShare(
            arCoreSession.session.createAnchor(node.getPose()),
            object : RoomCodeAndCloudAnchorIdListener.AnchorLoadListener {
                override fun onAnchorLoaded(anchorId: String) {
                    val location = gpsCoordinatesListener.getLastKnownLocation()
                    location?.let {
                        dataListener.onDataReady(Ar3dModelPostData(it, anchorId))
                    } ?: throw Throwable("GPS postLocation has not been determined, try again latter")
                }
            })
    }


    override fun retrieveTargetingPostData(hitPoint: Vector3): TargetingPostData {
        var ret: TargetingPostData? = null
        if (!targetingPostDataMap.contains(hitPoint)) {
            val location = gpsCoordinatesListener.getLastKnownLocation()
            location?.let {
                arCoreFrameEmitter.lastFrame()?.acquireCameraImage()?.use {
                    val bitmap = jpegToBitmap(
                        it, it.width, it.height
                    )?.rotate(-90f)
                    //  val snapshotS3Path = s3Cache.saveTempFile(bitmap!!.asJpeg()!!)
                    ret = TargetingPostData(
                        location,
                        bitmap!!//,
                        //snapshotS3Path
                    ).apply {
                        Timber.d("retrieveTargetingPostData success!")
                        targetingPostDataMap[hitPoint] = this
                    }
                } ?: throw Throwable("Ar lastFrame is not received, move camera a bit")
            } ?: throw Throwable("GPS postLocation has not been determined, try again latter")
        } else {
            ret = targetingPostDataMap[hitPoint]!!
        }
        return ret!!
    }

}
