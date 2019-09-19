package us.cyberstar.data.storage

import android.content.Context
import com.google.ar.core.CameraIntrinsics
import com.google.protobuf.MessageLite
import proxy.Proxy
import timber.log.Timber
import us.cyberstar.common.utils.generateFileNameUnique
import us.cyberstar.data.ArEntityCache
import us.cyberstar.data.entity.LoadWorldReplyEntity
import us.cyberstar.data.entity.telemetry.*
import us.cyberstar.data.mapper.mapToLoadWorldReply
import us.cyberstar.data.mapper.mapToLoadWorldReplyEntity
import us.cyberstar.data.mapper.telemetry.mapToSessionData
import us.cyberstar.data.utils.loadFromDisk
import us.cyberstar.data.utils.saveToDisk
import java.io.File
import javax.inject.Inject

internal class ArEntityCacheImpl @Inject constructor(
    private val context: Context
) : ArEntityCache {

    private val protoLogs = "_andr_pb"
    private val worldReplyFileName = "WORLD_REPLY"

    override fun saveArSession(
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
    ): File {
        val sessionData = mapToSessionData(
            sessionId,
            headFrames,
            finalFrames,
            startTimeStamp,
            cameraIntrinsics,
            sensorFrames,
            dataFrameArray,
            arPlaneArray,
            hwArray,
            arAugnmentedArray
        )
        return saveSessionToDisk(
            sessionId, sessionData!!
        )
    }


    override fun saveLoadWorldReply(
        sessionId: String,
        assetForDetectionEntityArray: List<CreatePostRequestEntity>
    ): File {
        val loadWorldReply = mapToLoadWorldReply(assetForDetectionEntityArray)
        return saveWorldReplyToDisk(sessionId, loadWorldReply)
    }


    override fun loadWorldReply(): LoadWorldReplyEntity? {
        var res: Proxy.LoadWorldReply? = null
        Timber.d("loadWorldReply")
        loadFromDisk(generateWorldFileName(""))?.use {
            res = Proxy.LoadWorldReply.parseFrom(it.readBytes())
        }
        return res?.let { mapToLoadWorldReplyEntity(it) }
    }


    private fun generateWorldFileName(sessionId: String): String {
        return generateFileNameUnique("", worldReplyFileName)
    }

    private fun generateLogFileName(sessionId: String): String {
        return generateFileNameUnique(sessionId, protoLogs)
    }


    private fun saveWorldReplyToDisk(sessionId: String, message: MessageLite) =
        saveToDisk(generateWorldFileName(sessionId), message)

    private fun saveSessionToDisk(sessionId: String, message: MessageLite) =
        saveToDisk(generateLogFileName(sessionId), message)
}