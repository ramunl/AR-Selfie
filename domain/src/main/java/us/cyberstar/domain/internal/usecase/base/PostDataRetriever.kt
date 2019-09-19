package us.cyberstar.domain.internal.usecase.base

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import io.reactivex.Single

interface PostDataRetriever {
    fun retrieveTargetingPostData(hitPoint: Vector3): TargetingPostData
    fun retrieveQuickPostData(): QuickPostData
    fun retrieveArModel3dPostData(node: Node, dataListener: Ar3dModelPostDataListener)
    interface Ar3dModelPostDataListener {
        fun onDataReady(postData: Ar3dModelPostData)
    }
}
