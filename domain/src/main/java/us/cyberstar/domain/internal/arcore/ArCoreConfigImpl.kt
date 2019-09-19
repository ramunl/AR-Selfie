package us.cyberstar.domain.internal.arcore

import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Config
import com.google.ar.core.Session
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.domain.external.arcore.ArCoreConfig
import javax.inject.Inject

///TODO NOT USED YET!!
internal class ArCoreConfigImpl @Inject constructor(
    private val session: Session,
    private val snackBarProvider: SnackBarProvider
) : ArCoreConfig {

    override var config: Config? = null

    override fun initConfig() {
        config = Config(session)
        config!!.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
        config!!.augmentedImageDatabase = AugmentedImageDatabase(session) // TODO move db to DI
        session.configure(config)
    }

    override fun switchCameraMode(photoVideoMode: Boolean) {
        config!!.focusMode = if(photoVideoMode) Config.FocusMode.AUTO else Config.FocusMode.FIXED
    }

}