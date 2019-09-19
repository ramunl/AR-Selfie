package us.cyberstar.presentation.feature.scenes.authScene.di

import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import us.cyberstar.common.PerActivity
import us.cyberstar.common.PerFragment
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.di.module.SocketModule
import us.cyberstar.domain.di.module.ProfileAuthModule
import us.cyberstar.presentation.delegate.toolbar.ToolbarDelegate
import us.cyberstar.presentation.feature.arFragment.view.ArFragmentImpl
import us.cyberstar.presentation.feature.phoneNumberEdit.di.PhoneNumberEditModule
import us.cyberstar.presentation.feature.phoneNumberEdit.view.PhoneNumberEditFragment
import us.cyberstar.presentation.feature.scenes.authScene.view.AuthActivity
import us.cyberstar.presentation.feature.scenes.devScene.view.DevActivity
import us.cyberstar.presentation.feature.scenes.mainScene.view.MainActivity
import us.cyberstar.presentation.feature.smsCodeConfirm.di.SMSCodeConfirmModule
import us.cyberstar.presentation.feature.smsCodeConfirm.view.SMSCodeConfirmFragment
import us.cyberstar.presentation.helpers.SnackBarProviderImpl

@Module
abstract class AuthViewModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        @PerActivity
        fun provideToolbarDelegate(activity: AuthActivity) = ToolbarDelegate(activity)

        @JvmStatic
        @Provides
        @PerActivity
        fun provideSnackBarHelper(activity: AuthActivity, schedulersProvider: SchedulersProvider): SnackBarProvider = SnackBarProviderImpl(activity, schedulersProvider)

    }

    @PerFragment
    @ContributesAndroidInjector(modules = [PhoneNumberEditModule::class])
    abstract fun providePhoneNumberEditFragmentFactory(): PhoneNumberEditFragment


    @PerFragment
    @ContributesAndroidInjector(modules = [SMSCodeConfirmModule::class])
    abstract fun provideSMSCodeConfirmFragmentFactory(): SMSCodeConfirmFragment


}
