package us.cyberstar.presentation.feature.scenes.authScene.view

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import us.cyberstar.arcyber.R
import us.cyberstar.presentation.base.BaseToolbarActivity
import us.cyberstar.presentation.feature.phoneNumberEdit.view.PhoneNumberEditFragment
import us.cyberstar.presentation.feature.scenes.authScene.presenter.AuthViewPresenter
import us.cyberstar.presentation.feature.scenes.mainScene.view.MainActivity
import us.cyberstar.presentation.feature.smsCodeConfirm.view.SMSCodeConfirmFragment
import us.cyberstar.presentation.helpers.addFragment
import javax.inject.Inject
import javax.inject.Provider
import android.app.Activity


class AuthActivity : BaseToolbarActivity(), AuthView {


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar(true)
    }


    override fun layoutRes() = R.layout.activity_auth

    override fun viewCreated(isRestoring: Boolean) {
        setupActionBar(true)
        if (!isRestoring) {
            addCoreRequestView()
        }
    }

    @Inject
    lateinit var providerPresenter: Provider<AuthViewPresenter>

    @InjectPresenter
    lateinit var presenter: AuthViewPresenter

    @ProvidePresenter
    fun providePresenter(): AuthViewPresenter = providerPresenter.get()


    fun addCoreRequestView() {
        title = "Enter your phone number"
        addFragment(R.id.container_auth, PhoneNumberEditFragment())
    }


    override fun showCodeConfirmView() {
        title = "Enter the code"
        addFragment(R.id.container_auth, SMSCodeConfirmFragment(), true)
    }


    override fun onTokenReceived(token: String?) {
        val intent = Intent()
        intent.putExtra("token", token)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onStop() {
        super.onStop()
        presenter.cleanSocketBackendListener()
    }

    override fun onStart() {
        super.onStart()
        presenter.setSocketBackendListener()
    }
}
