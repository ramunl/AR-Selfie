package us.cyberstar.presentation.feature.scenes.mainScene.interactor

import io.reactivex.subjects.BehaviorSubject
import us.cyberstar.domain.external.model.ArPostModel
import javax.inject.Inject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


class ArSceneInteractorImpl @Inject constructor() : ArSceneInteractor {

    data class SceneNavParam(val sceneState: SceneState, val arPostModel: ArPostModel? = null)

    enum class SceneState {
        None,
        Camera,
        Preview,
        Targeting,
        Open
        //,
       // Quick
    }

    class BehaviorSubjectProperty<T>(
        private val subject: BehaviorSubject<T>
    ) : ReadWriteProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T = subject.value!!
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = subject.onNext(value)
    }

    override val stateSubject = BehaviorSubject.create<SceneNavParam>()
    //public val firstNames: Observable<SceneState> get() = stateSubject.skip(1)
    //public var firstName: SceneState by BehaviorSubjectProperty(stateSubject)


//    private val sceneStateObserver = BaseObservable(SceneState.None)

//    var stateState by sceneStateObserver

    override fun changeView(sceneNavParam: SceneNavParam) {
        stateSubject.onNext(sceneNavParam)
        //sceneStateObserver.underlying = sceneState
    }

    /*override fun addSceneSateObserver(onChange: (old: SceneState, new: SceneState) -> Unit) {
        sceneStateObserver.addObserver(onChange)
    }*/
}