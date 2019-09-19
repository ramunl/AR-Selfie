package us.cyberstar.presentation.base

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import io.reactivex.disposables.CompositeDisposable

abstract class BasePresenter<View : MvpView> : MvpPresenter<View>() {

    lateinit var compositeDisposable: CompositeDisposable

    fun popBackStack(appCompatActivity: FragmentActivity?) {
        appCompatActivity?.supportFragmentManager?.popBackStack()
    }

    override fun attachView(view: View) {
        super.attachView(view)
        compositeDisposable = CompositeDisposable()
    }
    override fun detachView(view: View) {
        super.detachView(view)
        compositeDisposable.clear()
    }
}