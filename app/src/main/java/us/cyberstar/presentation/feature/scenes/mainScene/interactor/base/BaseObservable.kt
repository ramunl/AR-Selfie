package us.cyberstar.presentation.feature.scenes.mainScene.interactor.base

import kotlin.reflect.KProperty

class BaseObservable<T>(default: T) {

    var underlying = default
        set(value) {
            // Trigger on change
        }

    fun addObserver(onChange: (old: T, new: T) -> Unit) {/*etc*/
    }

    fun removeObserver(onChange: (old: T, new: T) -> Unit) {/*etc*/
    }

    operator fun getValue(thisRef: Any, prop: KProperty<*>): T = underlying
    operator fun setValue(thisRef: Any, prop: KProperty<*>, value: T) {
        underlying = value
    }
}