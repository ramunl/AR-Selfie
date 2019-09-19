package us.cyberstar.domain.internal.dictionary

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import us.cyberstar.common.utils.removeFrom
import us.cyberstar.data.entity.AssetForDetectionEntity
import us.cyberstar.data.entity.LoadWorldReplyEntity
import us.cyberstar.domain.external.dictionary.AssetForDetectionHashMapWrap
import us.cyberstar.domain.external.loader.ArWorldLoaderFabric
import us.cyberstar.domain.external.loader.grpc.telemetry.AssetForDetectionEmitter
import us.cyberstar.domain.internal.loader.grpc.telemetry.EntityEmitterBase
import javax.inject.Inject

/**
 * The purpose of this class is to listen for new AssetForDetection entities and put them into a hashMap
 * Who emits AssetForDetection entities? - it can be created in 2 ways:
 * 1) By user <-- TODO check probably we don't need this way here, because we need to tune root node only when we get new unknown entitites
 * 2) it can be received from server in LoadWorldrequest
 */
class AssetForDetectionHashMapWrapImpl @Inject constructor(
    private val compositeDisposable: CompositeDisposable,
    private val arWorldLoaderFabric: ArWorldLoaderFabric,

    private val assetForDetectionEmitter: AssetForDetectionEmitter,
    val schedulersProvider: us.cyberstar.common.external.SchedulersProvider
) : AssetForDetectionHashMapWrap {

    override fun clearAll() {
        disposable?.removeFrom(compositeDisposable)
        assetForDetectionEntityMap.clear()
    }

    override fun subscribeToAssetForDetections() {
        observe(arWorldLoaderFabric.getLoader())
        observe(assetForDetectionEmitter)
    }

    private val assetForDetectionEntityMap = HashMap<String, AssetForDetectionEntity>()


    var disposable: Disposable? = null

    private fun <T> observe(entityEmitter: EntityEmitterBase<T>) {
        disposable = entityEmitter.sourceObservable().subscribe(
            { entity ->
                when (entity) {
                    is AssetForDetectionEntity -> putEntity(entity)
                    is LoadWorldReplyEntity -> putEntity(entity)
                }
            },
            { Timber.e("sourceObservable $it") },
            { Timber.d("sourceObservable onComplete called") })
        disposable!!.addTo(compositeDisposable)
    }

    private fun putEntity(entity: LoadWorldReplyEntity) {
        Timber.d("putEntity for LoadWorldReplyEntity assets = ${entity.assetForDetectionArray.size}")
        for (asset in entity.assetForDetectionArray) {
            putEntity(asset)
        }
    }

    private fun putEntity(entity: AssetForDetectionEntity) {
        assetForDetectionEntityMap[entity.snapshotS3Path!!] = entity
        Timber.d("putEntity for ${entity.snapshotS3Path} size = ${assetForDetectionEntityMap.size}")
    }

    override fun getEntity(snapshot: String) = assetForDetectionEntityMap[snapshot]

}
