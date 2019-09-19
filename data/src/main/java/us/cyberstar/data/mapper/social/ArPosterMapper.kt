package us.cyberstar.data.mapper.social

import base_types.BaseTypes
import social.Social
import us.cyberstar.data.entity.social.ARPosterEntity
import us.cyberstar.data.entity.social.Ar3dModelEntity

fun mapToArPosterEntity(arPoster: Social.ARPoster): ARPosterEntity {
    return ARPosterEntity(arPoster.layoutImagesUrlsList, mapToAr3dModelEntity(arPoster.model)!!)
}

fun mapToArPoster(arPoster: ARPosterEntity): Social.ARPoster {
    var builder = Social.ARPoster.newBuilder()
    if (arPoster.layoutImagesUrls.isNotEmpty()) {
        builder = builder.addAllLayoutImagesUrls(arPoster.layoutImagesUrls)
    }
    arPoster.ar3DModelEntity?.let { builder.setModel(mapToAr3dModel(it)) }
    return builder.build()
}

fun mapToAr3dModel(model3DEntity: Ar3dModelEntity): BaseTypes.Model3d {
    return with(model3DEntity) {
        BaseTypes.Model3d.newBuilder().setId(id).setCategoryName(categoryName).setName(name)
            .setPreviewUrl(previewUrl)
            .setUploadTs(timeStamp)
            .build()
    }
}

fun mapToAr3dModelEntityList(model3dList:List<BaseTypes.Model3d>):List<Ar3dModelEntity> {
    return model3dList.map { mapToAr3dModelEntity(it) }
}

fun mapToAr3dModelEntity(model3d: BaseTypes.Model3d): Ar3dModelEntity {
    return with(model3d) {
        Ar3dModelEntity(
            id,
            url,
            name,
            previewUrl,
            uploadTs,
            categoryName
        )
    }
}