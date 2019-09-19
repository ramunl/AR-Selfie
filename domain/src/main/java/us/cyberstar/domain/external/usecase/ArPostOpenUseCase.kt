package us.cyberstar.domain.external.usecase

import us.cyberstar.domain.external.model.ArPostModel

interface ArPostOpenUseCase {
    interface PostNodeTapListener {
        fun onPostNodeTapped(arPostModel: ArPostModel)
    }
    fun startUseCase(postNodeTapListener: PostNodeTapListener)
    fun stopUseCase()
}