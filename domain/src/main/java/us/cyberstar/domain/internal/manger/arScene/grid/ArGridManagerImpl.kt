package us.cyberstar.domain.internal.manger.arScene.grid

import com.google.ar.core.Plane
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import us.cyberstar.common.utils.removeFrom
import us.cyberstar.data.entity.telemetry.ArPlaneEntity
import us.cyberstar.data.ext.distanceFromVector
import us.cyberstar.data.ext.translationVector3
import us.cyberstar.domain.NodeSize
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.arcore.ArCoreScene
import us.cyberstar.domain.external.arcore.ArCoreSession
import us.cyberstar.domain.external.factory.CellColor
import us.cyberstar.domain.external.loader.grpc.telemetry.PlanesEmitter
import us.cyberstar.domain.external.factory.RenderableFactory
import us.cyberstar.domain.external.manger.arScene.ArGridManager
import us.cyberstar.domain.external.provider.RootNodeProvider
import us.cyberstar.domain.internal.model.CellNode
import us.cyberstar.domain.internal.model.cellMargin
import us.cyberstar.domain.internal.utils.asVector3
import us.cyberstar.domain.internal.utils.calculateNormalToPlane
import us.cyberstar.domain.isCollided
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicReference

/**
 * This class is responsible for grid building
 */
abstract class ArGridManagerImpl constructor(
    override val arCoreFrameEmitter: ArCoreFrameEmitter,
    protected val compositeDisposable: CompositeDisposable,
    protected val arCoreScene: ArCoreScene,
    protected val arCoreSession: ArCoreSession,
    protected val rootNodeProvider: RootNodeProvider,
    protected val renderableFactory: RenderableFactory,
    protected val schedulersProvider: us.cyberstar.common.external.SchedulersProvider,
    protected val planesEmitter: PlanesEmitter
) : ArGridManager(arCoreFrameEmitter) {

    override val cellNodeList = mutableListOf<CellNode>()
    override var cellsVisible: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                for(cell in cellNodeList) {
                    cell.isVisible = value
                }
            }
        }

    var planesEmitterDisposable: Disposable? = null;

    override fun dropCellColors(selectedCell: CellNode?) {
        try {
            for (gridNode in gridAnchorNodes) {
                for (cellNode in gridNode.anchorNodeRef.get().children) {
                    if (cellNode != selectedCell) {
                        schedulersProvider.ui().scheduleDirect {
                            cellNode.renderable =
                                renderableFactory.getCellRenderable(CellColor.White)
                        }
                    }
                }
            }
        } catch (e: Throwable) {
            Timber.d("$e TODO, fix it")
        }
    }

    override fun destroyGrid() {
        if (planesEmitterDisposable == null) {
            Timber.d("destroyGrid")
            unsubscribeFromArCoreFrames()
            for (anchorNode in gridAnchorNodes.iterator()) {
                anchorNode.anchorNodeRef.get().anchor?.detach()
            }
            cellNodeList.clear()
            gridAnchorNodes.clear()
            rootNodeProvider.destroyGrid()
            planesEmitterDisposable?.removeFrom(compositeDisposable)
            planesEmitterDisposable = null
        }
    }

    private var arSession: Session = arCoreSession.session

    class GridNode(val parentPlane: ArPlaneEntity, anchorNode: AnchorNode) {
        val anchorNodeRef = AtomicReference<AnchorNode>(anchorNode)
    }

    override val gridAnchorNodes = CopyOnWriteArrayList<GridNode>()

    override fun createGrid() {
        if (planesEmitterDisposable == null) {
            Timber.d("createGrid")
            planesEmitterDisposable = planesEmitter.sourceObservable().subscribe(
                { entity -> onPlanesUpdated(entity) },
                { Timber.e("sourceObservable $it") },
                { Timber.d("sourceObservable onComplete called") })
                .addTo(compositeDisposable) // TODO clean it where it needs to be
            subscribeToArCoreFrames()
        }
    }

    override fun onUpdate(frameTime: FrameTime) {
        synchronized(this) {
            if (planesUpdated &&
                arCoreFrameEmitter.lastFrame()?.camera?.trackingState == TrackingState.TRACKING
            ) {
                planesUpdated = false
                if (renderableFactory.isRenderableLoaded()) {
                    schedulersProvider.ui().scheduleDirect { refreshGrid() }
                }
            }
        }
    }

    abstract fun isVerticalPlane(): Boolean

    val planesNew = CopyOnWriteArrayList<ArPlaneEntity>()
    var planesUpdated = false
    private fun onPlanesUpdated(planesNew: Collection<ArPlaneEntity>) {
        var updated = false
        for (plane in planesNew) {
            if (((plane.type == Plane.Type.VERTICAL && isVerticalPlane()) ||
                        (plane.type != Plane.Type.VERTICAL && !isVerticalPlane()))
            ) {
                this.planesNew.add(plane)
                updated = true
            }
        }
        planesUpdated = updated
    }


    private fun refreshGrid() {
        for (planeNew in planesNew) {
            //  var ignorPlane = false
            val planeNewPos = planeNew.pose.translationVector3()
            var gridAnchorToUse: GridNode? = null
            for (gridAnchorNode in gridAnchorNodes) {
                val distance = gridAnchorNode.parentPlane.pose.translation.asVector3()
                    .distanceFromVector(planeNewPos)
                if (distance < 1.0f) {
                    //    Timber.d("distance = $distance, using old grid anchor node $gridAnchorNode")
                    gridAnchorToUse = gridAnchorNode
                }
            }
            if (gridAnchorToUse == null) {
                val anchorNode = AnchorNode(arSession.createAnchor(planeNew.pose))
                gridAnchorToUse =
                    GridNode(
                        planeNew,
                        anchorNode
                    )
                rootNodeProvider.getGrid().addChild(anchorNode)
                gridAnchorNodes.add(gridAnchorToUse)
            }
            gridAnchorToUse.anchorNodeRef.get().apply {
                addCellsForPlaneFound(
                    NodeSize.CELL_NODE_SMALL,
                    this,
                    planeNew
                )
                if (children.isEmpty()) {
                    anchor?.detach()
                    rootNodeProvider.getGrid().removeChild(this)
                }
            }
        }
    }


    private fun addCellsForPlaneFound(
        cellType: NodeSize,
        anchorNode: AnchorNode,
        parentPlane: ArPlaneEntity
    ) {
        //Timber.d("addCellsForPlaneFound isVisible $isVisible")
        val normalToPlane =
            with(calculateNormalToPlane(parentPlane.pose)) { Vector3(this[0], this[1], this[2]) }
        val worldRotation = Quaternion.lookRotation(normalToPlane, Vector3.up())

        val cellNodeRenderable = renderableFactory.getCellRenderable(CellColor.White)

        val planeWidth = parentPlane.extentX
        val planeHeight = parentPlane.extentZ

        //Timber.d("Plane found $parentPlane ($planeWidth, $planeHeight)")
        val cellSize = cellType.getNodeSize()!!
        val colCount = planeWidth / cellSize.x
        val rawCount = planeHeight / cellSize.y
        //  Timber.d("cell size $cellSize")
        //  Timber.d(" colCount $colCount rawCount $rawCount")
        for (raw in 0..rawCount.toInt()) {
            for (col in 0..colCount.toInt()) {
                with(
                    CellNode(
                        cellType,
                        "$cellType$raw$col",
                        parentPlane,
                        Vector3(col * (cellSize.x + cellMargin), cellSize.z, raw * (cellSize.y + cellMargin)),
                        cellNodeRenderable,
                        cellsVisible
                    )
                ) {
                    anchorNode.addChild(this)
                    this.worldRotation = worldRotation
                    if (isCollided(this@ArGridManagerImpl)) {
                        anchorNode.removeChild(this)
                    } else {
                        cellNodeList.add(this)
                    }
                }
            }
        }
    }

    internal class SortablePlane(val distance: Float, val plane: Plane)
}


