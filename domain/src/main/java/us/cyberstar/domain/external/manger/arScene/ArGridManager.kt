package us.cyberstar.domain.external.manger.arScene

import us.cyberstar.domain.external.ArCoreFrameSubscriber
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.provider.RootNodeProvider
import us.cyberstar.domain.internal.manger.arScene.grid.ArGridManagerImpl
import us.cyberstar.domain.internal.manger.arScene.grid.VertGridManagerImpl
import us.cyberstar.domain.internal.model.CellNode
import us.cyberstar.domain.internal.model.PostNode
import java.util.concurrent.CopyOnWriteArrayList

abstract class ArGridManager (arCoreFrameEmitter: ArCoreFrameEmitter) :
    ArCoreFrameSubscriber(arCoreFrameEmitter) {
    abstract fun createGrid()
    abstract fun destroyGrid()
    abstract fun dropCellColors(selectedCell: CellNode?)
    abstract fun getNewInstance(rootNodeProvider: RootNodeProvider): ArGridManager
    abstract var cellsVisible: Boolean

    abstract val cellNodeList: MutableList<CellNode>
    abstract val gridAnchorNodes: CopyOnWriteArrayList<ArGridManagerImpl.GridNode>
}