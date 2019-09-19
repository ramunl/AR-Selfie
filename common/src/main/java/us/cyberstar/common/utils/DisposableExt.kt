package us.cyberstar.common.utils

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun Disposable.removeFrom(compositeDisposable: CompositeDisposable?): Disposable
        = apply { compositeDisposable?.remove(this) }