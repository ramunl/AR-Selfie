package us.cyberstar.data

import com.google.ar.core.CameraIntrinsics
import us.cyberstar.data.entity.LoadWorldReplyEntity
import us.cyberstar.data.entity.telemetry.*
import java.io.File

interface ArEntityCache {

    fun loadWorldReply(): LoadWorldReplyEntity?

    fun saveArSession(
        sessionId: String,
        startTimeStamp: Long?,
        cameraIntrinsics: CameraIntrinsics?,
        sensorFrames: MutableList<SensorFrameEntity>,
        dataFrameArray: MutableList<DataFrameEntity>,
        arPlaneArray: MutableList<ArPlaneEntity>,
        hwArray: MutableList<HardwareFrameEntity>,
        arAugnmentedArray: ArrayList<DetectedAssetEntity>,
        headFrames: ArrayList<SessionHeadEntity>,
        finalFrames: ArrayList<SessionFinalEntity>
    ): File

    fun saveLoadWorldReply(
        sessionId: String,
        assetForDetectionEntityArray: List<CreatePostRequestEntity>
    ): File
}