package us.cyberstar.presentation.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.MvpAppCompatActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

abstract class BaseActivity : MvpAppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    protected lateinit var compositeDisposable: CompositeDisposable


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        compositeDisposable = CompositeDisposable()
        setContentView(layoutRes())
        viewCreated(savedInstanceState != null)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }


    override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector

    @LayoutRes
    protected abstract fun layoutRes(): Int

    protected abstract fun viewCreated(isRestoring: Boolean)


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
