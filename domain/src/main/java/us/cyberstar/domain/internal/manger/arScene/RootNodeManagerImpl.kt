package us.cyberstar.domain.internal.manger.arScene

import ArQuaternion
import ArVector3
import com.cyber.math.Matrix3
import com.cyber.math.Matrix4
import com.google.ar.sceneform.math.Vector3
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.apache.commons.math3.linear.ArrayRealVector
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.common.utils.removeFrom
import us.cyberstar.data.entity.telemetry.DetectedAssetEntity
import us.cyberstar.data.ext.andrMatrix4
import us.cyberstar.domain.external.dictionary.AssetForDetectionHashMapWrap
import us.cyberstar.domain.external.loader.grpc.telemetry.AugmentedImageEmitter
import us.cyberstar.domain.external.manger.arScene.RootNodeManager
import us.cyberstar.domain.external.provider.RootNodeProvider
import us.cyberstar.domain.internal.utils.*
import javax.inject.Inject
import kotlin.math.PI
import kotlin.math.atan2

/**
 * The class controls root node position, listens for discovered "augmented" images
 * and tunes root node position
 */
class RootNodeManagerImpl @Inject constructor(
    private val compositeDisposable: CompositeDisposable,
    private val rootNodeProvider: RootNodeProvider,
    private val assetForDetectionHashMapWrap: AssetForDetectionHashMapWrap,
    private val snackBarProvider: SnackBarProvider,
    val augmentedImageEmitter: AugmentedImageEmitter,
    val schedulersProvider: SchedulersProvider
) : RootNodeManager {


    private var disposible: Disposable? = null

    override fun getNewInstance(rootNodeProvider: RootNodeProvider): RootNodeManager =
        RootNodeManagerImpl(
            compositeDisposable,
            rootNodeProvider,
            assetForDetectionHashMapWrap,
            snackBarProvider,
            augmentedImageEmitter,
            schedulersProvider
        )

    override fun unSubscribeFromAugmentedImgDbChanges() {
        disposible?.removeFrom(compositeDisposable)
        assetForDetectionHashMapWrap.clearAll()
    }

    override fun subscribeToAugmentedImgDbChanges() {
        Timber.d("subscribing to augmented Img db changes..")
        //repositionTest()
        disposible = augmentedImageEmitter.sourceObservable()
            .observeOn(schedulersProvider.io())
            .subscribeOn(schedulersProvider.io())
            .subscribe(
                { entity -> onAugmentedImageFound(entity) },
                { Timber.e(it) },
                { Timber.d("onComplete") })

        disposible!!.addTo(compositeDisposable)

        assetForDetectionHashMapWrap.subscribeToAssetForDetections()
    }


    override fun removeAllNodes() {
        Timber.d("removeAllNodes")
        rootNodeProvider.destroyGrid()
        rootNodeProvider.removeAllNodes()
    }


    private fun onAugmentedImageFound(augmentedImages: Collection<DetectedAssetEntity>) {
        if (augmentedImages.isNotEmpty()) {
            Timber.d("Augmented images found amount = ${augmentedImages.size}")
            for (augImg in augmentedImages) {
                val asset = assetForDetectionHashMapWrap.getEntity(augImg.name)
                snackBarProvider.showMessage("Asset for AugImg found! Run Root node reposition..")
                asset?.let {
                    Timber.d("AssetForDetection found for ${augImg.name} !!!")
                    synchronized(this) {
                        var assetTransform = asset.sessionToAssetTransform
                        val currentTransform = augImg.pose.andrMatrix4()
                        repositionLoadedSessionRootNode(assetTransform, currentTransform)
                        rootNodeProvider.nodeIsVisible = true
                    }
                }
            }
        }
    }

    //MARK: Qourum algoritm
    private var balancedLoadedSessionVectors = mutableListOf<TransQuatWrap>()
    private var balancedRotation = mutableListOf<Vector3>()

    fun repositionTest() {


        val assetTransform = floatArrayOf(
            -0.7452905f, 0f, 0.66673976f, -0.12658735f,
            -0.6536692f, -0.19703656f, -0.7306801f, -1.4721816f,
            0.13137215f, -0.9803962f, 0.14684944f, -0.168006f,
            0.0f, 0.0f, 0.0f, 1.0f
        )
        val currentTransform = floatArrayOf(
            0f, -0.99999994f, 0.0f, -0.66139525f,
            0.0f, 0.0f, 1.0f, -0.15632954f,
            -0.99999994f, 0f, 0.0f, 0.024959194f,
            0f, 0.0f, 0.0f, 1.0f
        )

        val assetTransform1 = floatArrayOf(
            -0.9388739f, 0f, 0.34426123f, -0.68752325f,
            0f, 1.0f, 0f, -0.23365422f,
            -0.34426123f, 0f, -0.9388739f, -1.8811344f,
            0.0f, 0.0f, 0.0f, 1.0f
        )
        val currentTransform1 = floatArrayOf(
            0.998976f, -0.04185672f, -0.017174222f, -0.5768278f,
            0.00024f, 0.38452476f, -0.9231146f, -0.09960924f,
            0.045242466f, 0.9221652f, 0.38414115f, -2.1543198f,
            0.0f, 0.0f, 0.0f, 1.0f
        )

        repositionLoadedSessionRootNode(
            Matrix4(assetTransform),
            Matrix4(currentTransform)
        )
        repositionLoadedSessionRootNode(
            Matrix4(assetTransform1),
            Matrix4(currentTransform1)
        )

        /*
        assetTransform:
            [-0.7452905,0,0.66673976,-0.12658735]
            [-0.6536692,-0.19703656,-0.7306801,-1.4721816]
            [0.13137215,-0.9803962,0.14684944,-0.168006]
            [0.0,0.0,0.0,1.0]
        currentTransform
            [0,-0.99999994,0.0,-0.66139525]
            [0.0,0.0,1.0,-0.15632954]
            [-0.99999994,0,0.0,0.024959194]
            [0.0,0.0,0.0,1.0]
         */
    }

    private fun repositionLoadedSessionRootNode(
        assetTransform: Matrix4,
        currentTransform: Matrix4
    ) {
        Timber.d("repositionLoadedSessionRootNode")
        Timber.d("assetTransform: ${assetTransform.values}")
        Timber.d("currentTransform: ${currentTransform.values}")

        var currentTransformModified = currentTransform

        var restoredNormal = ArVector3(0f, 0f, -1f)

        var transform3x3 = with(currentTransform) {
            Matrix3(
                floatArrayOf(
                    values[Matrix4.M11], values[Matrix4.M21], values[Matrix4.M31],
                    values[Matrix4.M12], values[Matrix4.M22], values[Matrix4.M32],
                    values[Matrix4.M13], values[Matrix4.M23], values[Matrix4.M33]
                )
            )
        }
        val rotationMatrix = Matrix3(
            floatArrayOf(
                1f, 0f, 0f,
                0f, 0f, 1f,
                0f, -1f, 0f
            )
        )

        transform3x3 = transform3x3.multiply(rotationMatrix)

        restoredNormal = transform3x3.mul(restoredNormal) //rotate it to pi/2

        val restoredNormalProjected = ArVector3(restoredNormal.x, 0f, restoredNormal.z)

        transform3x3 = Matrix3(
            floatArrayOf(
                1f, 0f, 0f,
                0f, 0f, 1f,
                0f, -1f, 0f
            )
        )
        //matrix rotated to pi/2
        currentTransformModified.values[Matrix4.M11] = 1f
        currentTransformModified.values[Matrix4.M21] = 0f
        currentTransformModified.values[Matrix4.M31] = 0f

        currentTransformModified.values[Matrix4.M12] = 0f
        currentTransformModified.values[Matrix4.M22] = 0f
        currentTransformModified.values[Matrix4.M32] = 1f

        currentTransformModified.values[Matrix4.M13] = 0f
        currentTransformModified.values[Matrix4.M23] = -1f
        currentTransformModified.values[Matrix4.M33] = 0f

        var zeroNormalProjected = ArVector3(0f, 0f, -1f)

        transform3x3 = transform3x3.multiply(rotationMatrix)

        zeroNormalProjected = transform3x3.mul(zeroNormalProjected)

        val vectorMultiply =
            restoredNormalProjected.mul(zeroNormalProjected) //vector multiply is a vector

        val sign: Float = with(vectorMultiply) {
            if (y > 0) 1f
            else if (y < 0) -1f
            else 0f
        }

        val vectorDot = restoredNormalProjected.dot(zeroNormalProjected) //scalar

        val normOne =
            with(vectorMultiply) {
                Vector3D(
                    x.toDouble(),
                    y.toDouble(),
                    z.toDouble()
                )
            }.norm1//the result is length

        val angle =
            atan2(normOne, vectorDot.toDouble()) //angle between asset normal and zero normal

        //if angle is 0 - > 0
        val angleDegree = Math.toDegrees(angle);

        val matRotationAngle = Matrix4(
            ArQuaternion(
                ArVector3(0f, 0f, sign),
                angleDegree.toFloat()
            )
        )

        currentTransformModified = currentTransformModified.multiply(matRotationAngle)


        val tmp = FloatArray(16)

        ArQuaternion(ArVector3(1f, 0f, 0f), (Math.toDegrees(PI) / 2f).toFloat()).toMatrix(tmp)

        val rotMat = Matrix4(tmp)

        var transformInverse = assetTransform.multiply(rotMat) //rotate saved asset to pi/2

        transformInverse = transformInverse.inv()

        var mulTransform =
            currentTransformModified.multiply(transformInverse) //detected asset verticalization, the result is transform one session to another


        //mulTransform = mulTransform.multiply(transform4x4_pi_2)
        Timber.d("mulTransform: ${mulTransform.values}")

        val quatTrans = matToQuatTrans2(mulTransform)
        //val axisAngle = quatTrans.axisAngle()

        val vectors = balanceLoadedSessionVectors(quatTrans)
        snackBarProvider.showMessage("repos ${vectors.position}, ${vectors.axisAngle()}")
        rootNodeProvider.setRootPos(vectors)
        //rootNodeProvider.setRootPos(quatTrans)
    }

    private fun balanceLoadedSessionVectors(newElement: TransQuatWrap): TransQuatWrap {

        if (balancedLoadedSessionVectors.size > 10) {
            balancedLoadedSessionVectors.removeAt(0)
            balancedRotation.removeAt(0)
        }
        balancedLoadedSessionVectors.add(newElement)

        if (balancedRotation.isEmpty()) {
            balancedRotation.add(newElement.rotation())
        } else {

            var delta = 0.0

            delta =
                (balancedLoadedSessionVectors.last().rotation().x - balancedRotation.last().x).toDouble()
            if (delta <= -PI) {
                delta += PI * 2
            }

            if (delta > PI) {
                delta -= PI * 2
            }
            val dx = balancedRotation.last().x + delta


            delta =
                (balancedLoadedSessionVectors.last().rotation().y - balancedRotation.last().y).toDouble()
            if (delta <= -PI) {
                delta += PI * 2
            }

            if (delta > PI) {
                delta -= PI * 2
            }
            val dy = balancedRotation.last().y + delta

            delta =
                (balancedLoadedSessionVectors.last().rotation().z - balancedRotation.last().z).toDouble()
            if (delta <= -PI) {
                delta += PI * 2
            }

            if (delta > PI) {
                delta -= PI * 2
            }
            val dz = balancedRotation.last().z + delta

            balancedRotation.add(Vector3(dx.toFloat(), dy.toFloat(), dz.toFloat()))
        }

        var averageRotationX = 0.0
        var averageRotationY = 0.0
        var averageRotationZ = 0.0

        var averagePosX = 0.0
        var averagePosY = 0.0
        var averagePosZ = 0.0

        for ((index, transQuatWrap) in balancedLoadedSessionVectors.withIndex()) {
            averagePosX += transQuatWrap.position.x
            averagePosY += transQuatWrap.position.y
            averagePosZ += transQuatWrap.position.z

            averageRotationX += balancedRotation[index].x
            averageRotationY += balancedRotation[index].y
            averageRotationZ += balancedRotation[index].z
        }

        averagePosX /= balancedLoadedSessionVectors.count()
        averagePosY /= balancedLoadedSessionVectors.count()
        averagePosZ /= balancedLoadedSessionVectors.count()

        averageRotationX /= balancedLoadedSessionVectors.count()
        averageRotationY /= balancedLoadedSessionVectors.count()
        averageRotationZ /= balancedLoadedSessionVectors.count()

        if (averageRotationX <= -PI) {
            averageRotationX += 2 * PI
        }
        if (averageRotationX > PI) {
            averageRotationX -= 2 * PI
        }

        if (averageRotationY <= -PI) {
            averageRotationY += 2 * PI
        }
        if (averageRotationY > PI) {
            averageRotationY -= 2 * PI
        }

        if (averageRotationZ <= -PI) {
            averageRotationZ += 2 * PI
        }
        if (averageRotationZ > PI) {
            averageRotationZ -= 2 * PI
        }
        var simd_float_3_out =
            ArrayRealVector(
                doubleArrayOf(averageRotationX, averageRotationY, averageRotationZ),
                0,
                3
            )

        val teta: Double = simd_float_3_out.l1Norm

        val tempVec = com.cyber.math.Vector3(
            floatArrayOf(
                averageRotationX.toFloat(),
                averageRotationY.toFloat(),
                averageRotationZ.toFloat()
            )
        )
        val tempVecNorm = tempVec.nor()

        return TransQuatWrap(
            floatArrayOf(averagePosX.toFloat(), averagePosY.toFloat(), averagePosZ.toFloat()),
            TransQuatWrap.AxisAngle(
                tempVecNorm.x,
                tempVecNorm.y,
                tempVecNorm.z,
                teta.toFloat()
            )
        )
    }

}


