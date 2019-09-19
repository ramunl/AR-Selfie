package us.cyberstar.domain.internal.utils

import com.google.ar.core.Frame
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Vector3
import us.cyberstar.data.mapper.utils.mapToRGB
import us.cyberstar.data.mapper.utils.mapToVectorFloat3
import us.cyberstar.data.model.PointWithColor


//this is a dirty hack to make possible to use method camera.worldToScreenPoint(coordinate)
var scene: Scene? = null

fun getColorPointsFromFrame(
    currentFrame: Frame?
): Collection<PointWithColor> {
    val pointWithColors = mutableListOf<PointWithColor>()
    currentFrame?.let { frame ->
        frame.acquirePointCloud()?.use { pointCloud ->
            val pointCloudData = pointCloud.points
            val pointCloudArray = FloatArray(pointCloud.points.limit())
            pointCloudData.get(pointCloudArray)
            val vectorSize = 4
            val points = pointCloudArray.size
            for (i in 0..points - vectorSize step vectorSize) {
                val coordinate = Vector3(
                    pointCloudArray[i],
                    pointCloudArray[i + 1],
                    pointCloudArray[i + 2]
                )
                pointWithColors.add(
                    PointWithColor(
                        mapToVectorFloat3(
                            coordinate
                        ), mapToRGB(0)!!
                    )
                )
            }
            //Timber.d("points $points pointWithColors = ${pointWithColors.size}")
            /*  if (pointCloudArray.isNotEmpty()) {
                  try {
                      frame.acquireCameraImage()?.use {
                          val nv21 = YUV_420_888toNV21(it)
                          val size = pointCloudArray.size
                          val vectorSize = 4
                          for (i in 0..size - vectorSize step vectorSize) {
                              val coordinate = Vector3(
                                  pointCloudArray[i],
                                  pointCloudArray[i + 1],
                                  pointCloudArray[i + 2]
                              )
                              val viewWidth = it.width
                              val viewHeight = it.height
                              val sceneWidth = scene!!.view.width
                              val sceneHeight = scene!!.view.height

                              val point2d = scene?.camera!!.worldToScreenPoint(coordinate)

                              val scalarX: Int = (point2d.x * viewWidth / sceneWidth).toInt()
                              val scalarY: Int = (point2d.y * viewHeight / sceneHeight).toInt()
                              try {
                                  //val pixel = bitmap!!.getPixel(scalarX, scalarY)
                                  val pixel = getPixel(nv21, it.width, it.height, scalarX, scalarY)
                                  val color = mapToRGB(pixel)
                                  pointWithColors.add(
                                      PointWithColor(
                                          mapToVectorFloat3(
                                              coordinate
                                          ), color!!
                                      )
                                  )
                              } catch (e: Exception) {
                                  Crashlytics.logException(e)
                                  Timber.w("coordinate ${coordinate.toString()} ")
                                  Timber.w("failed getPixel: pointCloudArray size $size and index is $i")
                                  Timber.w("getPixel($scalarX, $scalarY) failed with $e")
                              }
                          }
                      }

                  } catch (e: Exception) {
                      Crashlytics.logException(e)
                  }
              }*/
        }
    }
    return pointWithColors
}
