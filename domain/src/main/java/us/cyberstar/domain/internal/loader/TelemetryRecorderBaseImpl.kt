package us.cyberstar.domain.internal.loader

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import us.cyberstar.data.entity.telemetry.*
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry
import us.cyberstar.domain.external.arcore.ArSessionStartTimeProvider
import us.cyberstar.domain.external.loader.grpc.telemetry.*
import us.cyberstar.domain.internal.loader.grpc.telemetry.EntityEmitterBase
import java.util.concurrent.TimeUnit

/**
 * This is the base class for local/remote session data recorders
 * it subscribes to entity emitters amd provides abstract methods
 * to start/stop tracing and append entities to grpc stream or local file
 */

abstract class TelemetryRecorderBaseImpl constructor(
    private val hardwareEntityEmitter: HardwareEntityEmitter,
    private val schedulersProvider: us.cyberstar.common.external.SchedulersProvider,
    //private val dataFrameEntityEmitter: DataFrameEntityEmitter,
    private val augmentedImageEmitter: AugmentedImageEmitter,
    private val planesEmitter: PlanesEmitter,
    protected val arSessionStartTimeProvider: us.cyberstar.domain.external.arcore.ArSessionStartTimeProvider,
    private val headFinalEntityEmitter: HeadFinalEntityEmitter
) : TelemetryRecorderBase {

    val compositeDisposable = CompositeDisposable()

    override fun startTelemetry(headEntity: SessionHeadEntity) {
        ArEntityTelemetry.resetCounter()
        subscribe()
        appendEntity(headEntity)
        Timber.d("startTelemetry")
    }

    override fun stopAndSaveTelemetry(finalEntity: SessionFinalEntity) {
        appendEntity(finalEntity)
        unsubscribe()
        Timber.d("stopAndSaveTelemetry")
    }

    override fun resetCounter() {
        ArEntityTelemetry.resetCounter()
    }

    fun unsubscribe() {
        compositeDisposable.clear()
    }

    fun subscribe() {
        Timber.d(".......ArCoreEntityRecorder start listen.....")
        listenToEmitter(hardwareEntityEmitter as EntityEmitterBase<ArEntityTelemetry>)
        //listenToEmitter(dataFrameEntityEmitter as EntityEmitterBase<ArEntityTelemetry>)
        listenToEmitterList(planesEmitter as EntityEmitterBase<List<ArEntityTelemetry>>)
        listenToEmitterList(augmentedImageEmitter as EntityEmitterBase<List<ArEntityTelemetry>>)
    }

    private fun listenToEmitterList(entityEmitter: EntityEmitterBase<List<ArEntityTelemetry>>) {
        entityEmitter.sourceObservable()
            .throttleWithTimeout(100, TimeUnit.MILLISECONDS)
            .observeOn(schedulersProvider.io())
            .subscribeOn(schedulersProvider.io())
            .subscribe(
                { entities ->
                    for (entity in entities) {
                        appendEntity(entity)
                    }
                },
                { Timber.e(it) },
                { Timber.d("onComplete") })
            .addTo(compositeDisposable)
    }


    private fun listenToEmitter(entityEmitter: EntityEmitterBase<ArEntityTelemetry>) {
        entityEmitter.sourceObservable()
            .throttleWithTimeout(100, TimeUnit.MILLISECONDS)
            .observeOn(schedulersProvider.io())
            .subscribeOn(schedulersProvider.io())
            .subscribe(
                { entity ->
                    appendEntity(entity)
                },
                { Timber.e(it) },
                { Timber.d("onComplete") })
            .addTo(compositeDisposable)
    }
}