package us.cyberstar.presentation.feature.scenes.mainScene.interactor

import io.reactivex.subjects.BehaviorSubject


interface ArSceneInteractor {
    //fun addSceneSateObserver(onChange: (old: ArSceneInteractorImpl.SceneState, new: ArSceneInteractorImpl.SceneState) -> Unit)
    fun changeView(sceneNavParam: ArSceneInteractorImpl.SceneNavParam)

    val stateSubject: BehaviorSubject<ArSceneInteractorImpl.SceneNavParam>
}