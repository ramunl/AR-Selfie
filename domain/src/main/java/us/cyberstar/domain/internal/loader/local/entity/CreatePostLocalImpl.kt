package us.cyberstar.domain.internal.loader.local.entity

import io.reactivex.Maybe
import timber.log.Timber
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.ArEntityCache
import us.cyberstar.data.SessionIdProvider
import us.cyberstar.data.entity.telemetry.*
import us.cyberstar.domain.external.loader.local.CreatePostLocal
import javax.inject.Inject

/**
 * The class saves session data ( post, plane, assets)
 * to local storage
 */
internal class CreatePostLocalImpl @Inject constructor(
    private val sessionIdProvider: SessionIdProvider,
    private val snackBarProvider: SnackBarProvider,
    private val arEntityCache: ArEntityCache
) : CreatePostLocal {

    private val createPostRequestEntityArray = ArrayList<CreatePostRequestEntity>()


    override fun createPostRequest(entity: CreatePostRequestEntity): Boolean {
        createPostRequestEntityArray.add(entity)
        saveLoadWorldReplyToFile()
        return true
    }

    private fun saveLoadWorldReplyToFile(): Maybe<String> {
        return Maybe.create<String> { fileEmitter ->
            try {
                Timber.d("stop recording And Save createPostRequest array = ${createPostRequestEntityArray.size} to file")
                try {
                    if (createPostRequestEntityArray.size > 0) {
                        val worldReply = arEntityCache.saveLoadWorldReply(
                            sessionIdProvider.sessionId()!!,
                            createPostRequestEntityArray
                        )
                        snackBarProvider.showMessage("Data saved! createPostRequestEntity = ${createPostRequestEntityArray.size} world size = ${worldReply.length()}")
                        // arFinalEntity = null
                    }
                } catch (e: Exception) {
                    snackBarProvider.showError(e.toString(), false)
                }
            } catch (e: Exception) {
                fileEmitter.onError(e)
            }
        }
    }

}