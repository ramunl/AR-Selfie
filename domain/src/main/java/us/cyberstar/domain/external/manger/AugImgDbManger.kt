package us.cyberstar.domain.external.manger

interface AugImgDbManger {
    fun removeImages()
    fun subscribeToAssetForDetection()
    fun unsubscribe()
}