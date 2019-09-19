package us.cyberstar.domain.internal.loader.grpc.telemetry

import com.google.ar.core.Plane
import com.google.ar.sceneform.FrameTime
import us.cyberstar.common.external.ResRepo
import us.cyberstar.data.entity.telemetry.ArPlaneEntity
import us.cyberstar.domain.R
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.loader.grpc.telemetry.PlanesEmitter
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

/**
 * The class is responsible for emitting detected planes
 * also it updates planesInfo string
 */

//TODO refactor this class, its name doesn't correspond the meanings
internal class PlanesEmitterImpl @Inject constructor(
    private val resRepo: ResRepo,
    arCoreFrameEmitter: ArCoreFrameEmitter
) : PlanesEmitter(arCoreFrameEmitter) {

    override var planesFound: Collection<Plane> = mutableListOf()

    override var planesInfo = AtomicReference(resRepo.getString(R.string.no_planes_found))

    override fun onUpdate(frameTime: FrameTime) {
        arCoreFrameEmitter.lastFrame()?.let {
            val planes = it.getUpdatedTrackables(Plane::class.java)
            if (planesFound != planes && planes.isNotEmpty()) {
                planesFound = planes
                val arPlaneEntities = mutableListOf<ArPlaneEntity>()
                for (plane in planes) {
                    with(plane) {
                        arPlaneEntities.add(
                            ArPlaneEntity(
                                type,
                                polygon,
                                anchors,
                                extentX,
                                extentZ,
                                centerPose
                            )
                        )
                    }
                    emitNext(arPlaneEntities)
                }
            }
            onPlanesUpdated(planesFound)
        }
    }

    private fun onPlanesUpdated(planes: Collection<Plane>) {
        val planesInfoBuilder = StringBuilder()
        if (planes.isEmpty()) {
            planesInfoBuilder.append(resRepo.getString(R.string.no_planes_found))
        } else {
            var verts = 0
            var hors = 0
            for (plane in planes) {
                if (plane.type == Plane.Type.VERTICAL) {
                    verts++
                } else {
                    hors++
                }
            }
            planesInfoBuilder.append("planes num= ${planes.size} v= $verts h = $hors \n")
        }
        planesInfo.set(planesInfoBuilder.toString())
    }
}

