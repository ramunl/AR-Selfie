package us.cyberstar.domain.internal.mapper

import us.cyberstar.common.utils.timeNow
import us.cyberstar.data.entity.social.ARPosterEntity
import us.cyberstar.data.entity.social.Ar3dModelEntity
import us.cyberstar.data.entity.social.ArPostEntity
import us.cyberstar.domain.external.helper.getNewId
import us.cyberstar.domain.external.model.*


fun mapToArPosterEntity(arPoster: ArPosterModel): ARPosterEntity {
    val ar3DModelEntity = Ar3dModelEntity(
        getNewId().toString(),
        arPoster.model3dUrl ?: "",
        "Test3dName_Android",
        "https://sketchfab.com/3d-models/l-4224a57447fe4d9ea13c363471b7a994",
        timeNow().toDouble(),
        "Test3dCategory_Android"
    )
    return ARPosterEntity(
        arPoster.imageUrls,
        ar3DModelEntity
    )
}

/*fun mapToArPosterModel(arPoster: ARPosterEntity): ArPosterModel {
    return ArPosterModel(arPoster.layoutImagesUrls)
}*/


//map from domain entity to UI model
//used in PostOpenUseCase
fun mapToPostModel(arPostEntity: ArPostEntity): ArPostModel {
    val arPostModel = ArPostModel(arPostEntity.title)
    arPostEntity.isQuick?.let {
        arPostModel.arPostType = if (it) ArPostType.QUICK else ArPostType.TARGETING
    }
    arPostEntity.postContentEntity?.let {
        with(it) {
            photoPostContentEntity?.let {
                arPostModel.photoModel =
                    ArPostPhotoModel(
                        it.photoWidth,
                        it.photoHeight,
                        it.photoUrl!!,
                        HashMap(it.thumbs)
                    )
            } ?: {
                videoPostContentEntity?.let {
                    arPostModel.videoModel =
                        ArPostVideoModel(
                            it.videoWidth,
                            it.videoHeight,
                            it.videoUrl!!,
                            it.fps,
                            HashMap(it.thumbs)
                        )
                }
            }()
        }
    }

    return arPostModel
}
