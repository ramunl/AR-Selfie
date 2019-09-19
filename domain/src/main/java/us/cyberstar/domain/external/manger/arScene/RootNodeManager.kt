package us.cyberstar.domain.external.manger.arScene

import us.cyberstar.domain.external.provider.RootNodeProvider


interface RootNodeManager {
    fun removeAllNodes()
    fun subscribeToAugmentedImgDbChanges()
    fun unSubscribeFromAugmentedImgDbChanges()
    fun getNewInstance(rootNodeProvider: RootNodeProvider): RootNodeManager
}