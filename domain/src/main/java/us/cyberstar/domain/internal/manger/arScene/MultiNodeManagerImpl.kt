package us.cyberstar.domain.internal.manger.arScene

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.utils.removeFrom
import us.cyberstar.data.entity.LoadWorldReplyEntity
import us.cyberstar.data.entity.MultipleLoadWorldReplyEntity
import us.cyberstar.domain.external.loader.ArWorldLoaderFabric
import us.cyberstar.domain.external.loader.grpc.telemetry.PostEntityEmitter
import us.cyberstar.domain.external.manger.arScene.CREATE_3D_ONLY
import us.cyberstar.domain.external.manger.arScene.MultiNodeManager
import us.cyberstar.domain.external.manger.arScene.NodeManager
import us.cyberstar.domain.internal.loader.grpc.telemetry.EntityEmitterBase
import java.util.ArrayList
import java.util.HashMap
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class MultiNodeManagerImpl @Inject constructor(
    private val currentSessionNodeManager: NodeManager,
    private val postEntityEmitter: PostEntityEmitter,
    private val arWorldLoaderFabric: ArWorldLoaderFabric,
    private val compositeDisposable: CompositeDisposable,
    private val schedulersProvider: SchedulersProvider
) : MultiNodeManager {

    override fun destroy() {
        clearNodeManagers()
    }

    private fun clearNodeManagers() {
        Timber.d("MultiNodeManager clearNodeManagers")
        for (manager in nodeManagers.values) {
            manager.destroy()
        }
        nodeManagers.clear()
    }

    private var postEntityEmitterDisposable: Disposable? = null
    private var arWorldLoaderDisposable: Disposable? = null

    private val nodeManagers = ConcurrentHashMap<String, NodeManager>()

    override fun unsubscribeFromPostCreated() {
        if (arWorldLoaderDisposable != null && postEntityEmitterDisposable != null) {
            currentSessionNodeManager.destroy()
            currentSessionNodeManager.unsubscribeFromArCoreFrames()
            Timber.d("MultiNodeManager unsubscribeFromPostCreated")
            postEntityEmitterDisposable?.removeFrom(compositeDisposable)
            postEntityEmitterDisposable = null
            arWorldLoaderDisposable?.removeFrom(compositeDisposable)
            arWorldLoaderDisposable = null
        }
    }

    override fun subscribeToPostCreated() {
        if (arWorldLoaderDisposable == null && postEntityEmitterDisposable == null) {
            currentSessionNodeManager.create()
            currentSessionNodeManager.subscribeToArCoreFrames()
            arWorldLoaderDisposable = observe(arWorldLoaderFabric.getLoader())
            postEntityEmitterDisposable = observe(postEntityEmitter)
            Timber.d("MultiNodeManagerImpl subscribed")
        }
    }

    private fun <T> observe(entityEmitter: EntityEmitterBase<T>): Disposable {
        return entityEmitter.sourceObservable()
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.io())
            .subscribe({
                if (it is MultipleLoadWorldReplyEntity) {
                    onMultipleLoadWorldLoaded(it)
                }
            },
                { Timber.e("arSceneUserEntityEmitter $it") },
                { Timber.d("arSceneUserEntityEmitter onComplete called") })
            .addTo(compositeDisposable) // TODO clean it where it needs to be
    }

    private fun onMultipleLoadWorldLoaded(multipleLoadWorldReplyEntity: MultipleLoadWorldReplyEntity) {
        Timber.d("onMultipleLoadWorldLoaded")
        with(multipleLoadWorldReplyEntity.loadWorldReplyEntityList) {
            if (isNotEmpty()) {
                updateCurrentRootNodes(this)
            }
        }
    }

    private fun updateCurrentRootNodes(loadWorldReplies: List<LoadWorldReplyEntity>) {
        Timber.d("check and clean old worlds do we need to remove something ?")
        val loadWorldReplyMapNew = HashMap<String, LoadWorldReplyEntity>()
        for (reply in loadWorldReplies) {
            loadWorldReplyMapNew[reply.sessionId] = reply
            if (CREATE_3D_ONLY) {
                currentSessionNodeManager.updatePostsWithNewList(reply.arPostEntityArray)
            } else {
                if (nodeManagers.containsKey(reply.sessionId)) {
                    //old worlds list contains a new one
                    //check timestamp and update it
                    nodeManagers[reply.sessionId]!!.updatePostsWithNewList(reply.arPostEntityArray)
                } else {
                    //add new world to current list
                    nodeManagers[reply.sessionId] = currentSessionNodeManager.getNewInstance()
                        .apply {
                            create()
                            updatePostsWithNewList(reply.arPostEntityArray)
                        }
                }
                //we need to collect session id's which loadWorldReplyMapNew doesn't contain
                val rootNodeSessionIdsToRemove = ArrayList<String>()
                for (sessionId in nodeManagers.keys) {
                    if (!loadWorldReplyMapNew.contains(sessionId)) {
                        rootNodeSessionIdsToRemove.add(sessionId)
                        Timber.d("remove world with sessionId")
                    }
                }
                //clean nodeManagers from worlds which don't exist in loadWorldReplyMapNew
                for (worldSessionToRemove in rootNodeSessionIdsToRemove) {
                    nodeManagers[worldSessionToRemove]!!.destroy()
                    nodeManagers.remove(worldSessionToRemove)
                }
            }
        }

    }

    override fun getInfo(): String {
        val info = StringBuffer()
        info.append("nodeManagers: ${nodeManagers.size + 1}\n")
        with(currentSessionNodeManager) {
            info.append("posts:${getPostNodesCount()} ${getPostNodesInfo()}\nisEnabled = ${isEnabled()}\n")
        }
        for (nodeManager in nodeManagers) {
            with(nodeManager) {
                info.append("posts:${value.getPostNodesCount()} ${value.getPostNodesInfo()} isEnabled =  ${value.isEnabled()} \n")
            }
        }

        info.append()
        return info.toString()
    }

}