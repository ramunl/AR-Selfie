package us.cyberstar.domain.internal.loader.grpc.telemetry

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

abstract class EntityEmitterBase<T>() {
    private val entityEmitter by lazy { BehaviorSubject.create<T>() } //TODO parametrize this shit!

    fun sourceObservable(): Observable<T> = entityEmitter//.distinctUntilChanged()

    fun emitNext(entity: T) {
        if (entityEmitter.hasObservers())
            entityEmitter.onNext(entity)
    }

    fun callOnComplete() {
        if (entityEmitter.hasObservers())
            entityEmitter.onComplete()
    }
}