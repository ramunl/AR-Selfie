package us.cyberstar.domain.internal.arcore

import android.app.Activity
import com.google.ar.core.ArCoreApk
import com.google.ar.core.exceptions.UnavailableApkTooOldException
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException
import com.google.ar.core.exceptions.UnavailableSdkTooOldException
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import timber.log.Timber
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.domain.external.arcore.ArCoreSupportVerifier
import javax.inject.Inject

internal class ArCoreSupportVerifierImpl @Inject constructor(
    private val snackBarProvider: SnackBarProvider
) : ArCoreSupportVerifier {

    private var installRequested = false

    override fun isDeviceSupported(
        activity: Activity,
        sessionCreationListener: ArCoreSupportVerifier.ArCoreSupportCheckListener
    ) {
        var exception: Exception? = null
        var message: String? = null
        try {
            when (ArCoreApk.getInstance()?.requestInstall(activity, installRequested)) {
                ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                    installRequested = true
                }
                ArCoreApk.InstallStatus.INSTALLED -> {
                    // ARCore requires camera permissions to operate. If we did not yet obtain runtime
                    // permission on Android M and above, now is a good time to ask the user for it.
                    sessionCreationListener.isSupported()
                }
            }
        } catch (e: UnavailableUserDeclinedInstallationException) {
            message = "Please install ARCore"
            exception = e
        } catch (e: UnavailableApkTooOldException) {
            message = "Please update ARCore"
            exception = e
        } catch (e: UnavailableSdkTooOldException) {
            message = "Please update this app"
            exception = e
        } catch (e: UnavailableDeviceNotCompatibleException) {
            message = "This device does not support AR"
            exception = e
        } catch (e: Exception) {
            message = "Failed to create AR session"
            exception = e
        }
        if (message != null) {
            snackBarProvider.showError(message, true)
            Timber.e("Exception creating session $exception")
        }
    }
}