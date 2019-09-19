package us.cyberstar.domain.internal.loader.local.entity

import android.content.Context
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.ArEntityCache
import us.cyberstar.data.entity.LoadWorldReplyEntity
import us.cyberstar.domain.external.loader.local.ArWorldLoaderFromFile
import javax.inject.Inject

internal class ArWorldLoaderFromFileImpl @Inject constructor(
    override val context: Context,
    override val snackBarProvider: SnackBarProvider,
    private val arEntityCache: ArEntityCache,
    val schedulersProvider: SchedulersProvider
) : ArWorldLoaderFromFile() {

    override fun stopSceneWorldUpdating() {
        Timber.d("stopSceneWorldUpdating local")

    }

    override fun startSceneWorldUpdating() {
        schedulersProvider.io().scheduleDirect {
            Timber.d("startSceneWorldUpdating from file")
            val worldReply: LoadWorldReplyEntity? = arEntityCache.loadWorldReply()
            worldReply?.let { reply ->
                //onLoadWorldReplyEntityReady(reply)
            } ?: showError("startSceneWorldUpdating (from file) failed")
        }
    }
}