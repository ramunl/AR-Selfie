package us.cyberstar.domain.external.loader

import us.cyberstar.domain.external.loader.base.CreatePostBase

interface CreatePostFabric {
    fun getPostCreateFabric(): CreatePostBase
}