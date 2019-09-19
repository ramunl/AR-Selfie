package us.cyberstar.presentation.base

import android.content.Context
import android.os.Bundle
import androidx.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import dagger.android.support.AndroidSupportInjection
import timber.log.Timber

abstract class  BaseFragment : MvpAppCompatFragment() {


    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onStart")
    }

    override fun onPause() {
        super.onPause()
        Timber.d("onPause")
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(layoutRes(), container, false)


    @LayoutRes
    protected abstract fun layoutRes(): Int

}
