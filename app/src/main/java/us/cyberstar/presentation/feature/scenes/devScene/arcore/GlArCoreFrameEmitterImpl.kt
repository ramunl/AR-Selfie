package us.cyberstar.presentation.feature.scenes.devScene.arcore

import com.google.ar.core.Frame
import com.google.ar.core.Session
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.arcore.ArCoreSession
import us.cyberstar.domain.external.loader.grpc.telemetry.OnUpdateListener
import javax.inject.Inject

class GlArCoreFrameEmitterImpl @Inject constructor(
    arCoreSession: ArCoreSession,
    compositeDisposable: CompositeDisposable
) : ArCoreFrameEmitter {
    override fun lastFrame(): Frame? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addUpdateListener(onUpdateListener: OnUpdateListener) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeUpdateListener(onUpdateListener: OnUpdateListener) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var lastFrame: Frame?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    private var session: Session = arCoreSession.session

    init {
        Timber.d("ArCoreFrameEmitterImpl created!")
        Timber.d("scene addOnUpdateListener added")
    }


    override fun addFrameListener() {

    }

    override fun removeFrameListener() {
       // lastFrame.set(null)

    }
}

