package us.cyberstar.presentation.feature.scenes.mainScene.arcore

import com.google.ar.core.Frame
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Scene
import io.reactivex.Scheduler
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.loader.grpc.telemetry.DataFrameEntityEmitter
import us.cyberstar.domain.external.loader.grpc.telemetry.OnUpdateListener
import us.cyberstar.presentation.feature.scenes.mainScene.arcore.scene.ArCoreSceneView
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import kotlin.concurrent.scheduleAtFixedRate

class ArCoreFrameEmitterImpl @Inject constructor(
    val schedulersProvider: SchedulersProvider,
    val arCoreSceneView: ArCoreSceneView
) : ArCoreFrameEmitter {

    private lateinit var scheduler: Scheduler

    var timer: Timer? = null
    var dataFrameCounter = 0

    private fun stopTimer() {
        timer?.let {
            scheduler.shutdown()
            Timber.e("...................ArCoreFrameEmitter stopTimer...................")
            it.cancel()
            timer = null
        }
    }

    private fun startTimer() {
        if (timer == null) {
            dataFrameCounter = 0
            scheduler = schedulersProvider.newThread()
            Timber.e("................ArCoreFrameEmitter startTimer.................")
            timer = Timer().apply {
                scheduleAtFixedRate(0, 100) {
                    scheduler.scheduleDirect {
                        for (onUpdateListener in onUpdateListenerArray) {
                            if (!(onUpdateListener is DataFrameEntityEmitter)) {//we update DataFrameEntityEmitter in Scene.OnUpdateListener
                                frameTime.get()?.let {
                                    onUpdateListener.onUpdate(it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    val atomicFrame = AtomicReference<Frame>()

    override fun lastFrame() = atomicFrame.get()
    var frameTime = AtomicReference<FrameTime>()

    private val onUpdateListenerArray = CopyOnWriteArrayList<OnUpdateListener>()

    private val onUpdateListener = Scene.OnUpdateListener {
        atomicFrame.set(arCoreSceneView.arSceneView.arFrame)
        frameTime.set(it)
        scheduler.scheduleDirect {
            for (onUpdateListener in onUpdateListenerArray) {
                if (onUpdateListener is DataFrameEntityEmitter) {
                    onUpdateListener.setCalcDescriptorsFlag(++dataFrameCounter % 3 == 0)
                    onUpdateListener.onUpdate(it)
                }
            }
        }
    }

    override fun addUpdateListener(onUpdateListener: OnUpdateListener) {
        Timber.d("scene addUpdateListener $onUpdateListener")
        if (!onUpdateListenerArray.contains(onUpdateListener)) {
            onUpdateListenerArray.add(onUpdateListener)
        }
    }

    override fun removeUpdateListener(onUpdateListener: OnUpdateListener) {
        Timber.d("scene removeUpdateListener $onUpdateListener")
        if (onUpdateListenerArray.contains(onUpdateListener)) {
            onUpdateListenerArray.remove(onUpdateListener)
        }
    }

    override fun addFrameListener() {
        Timber.d("scene addOnUpdateListener added")
        arCoreSceneView.arSceneView.scene.addOnUpdateListener(onUpdateListener)
        startTimer()
    }

    override fun removeFrameListener() {
        Timber.d("scene addOnUpdateListener removed")
        arCoreSceneView.arSceneView.scene.removeOnUpdateListener(onUpdateListener)
        stopTimer()
    }
}

/*

//   override var lastFrame = AtomicReference<Frame>()

     fun onUpdate(p0: FrameTime) {
        //arSceneView.arFrame?.let { lastFrame ->
        //emitNext(p0)
        //lastFrame.set(lastFrame)
        /*if(dirtyMode) {
            try {
                lastFrame.acquireCameraImage()?.use {
                    lastFrame.set(imageCopy(it)) // we use this shit to create AssetForDetection entity
                }
            } catch (e: Exception) {
                //ignore exception
            }
        }*/
        // }
    }
 */

