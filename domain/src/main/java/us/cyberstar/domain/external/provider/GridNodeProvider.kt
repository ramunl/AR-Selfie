package us.cyberstar.domain.external.provider

import com.google.ar.sceneform.Node

interface GridNodeProvider {
    fun getGrid(): Node
    fun destroyGrid()
}
