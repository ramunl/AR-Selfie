package us.cyberstar.domain.internal.loader.base

import android.content.Context
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.entity.MultipleLoadWorldReplyEntity
import us.cyberstar.domain.external.loader.base.ArWorldLoader

/**
 * This the base class for world loader (local/remote)
 */
abstract class ArWorldLoaderImpl : ArWorldLoader() {
    abstract val context: Context
    abstract val snackBarProvider: SnackBarProvider


    fun onLoadWorldReplyEntityReady(replyList: MultipleLoadWorldReplyEntity) {
        //snackBarProvider.showMessage("World is loaded! replyList=${replyList.loadWorldReplyEntityList.size} ")
        emitNext(replyList)
    }

    protected fun showError(errMsg: String) {
        snackBarProvider.showError(errMsg, false)
    }

}
