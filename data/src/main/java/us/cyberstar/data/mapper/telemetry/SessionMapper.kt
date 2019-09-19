package us.cyberstar.data.mapper.telemetry

import com.google.ar.core.CameraIntrinsics
import com.google.ar.sceneform.FrameTime
import proxy.Proxy
import session.Session
import us.cyberstar.data.entity.telemetry.*
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry.Companion.sessionFinalEntityCounter
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry.Companion.sessionHeadEntityCounter
import us.cyberstar.data.mapper.mapToMatrix3x3


fun mapToSessionData(
    sessionId: String,
    headFrames: ArrayList<SessionHeadEntity>,
    finalFrames: ArrayList<SessionFinalEntity>,
    videoStartTimeStamp: Long?,
    cameraIntrinsics: CameraIntrinsics?,
    sensorFrameEntityArray: Collection<SensorFrameEntity>,
    dataFrameArray: Collection<DataFrameEntity>,
    arPlainArray: Collection<ArPlaneEntity>?,
    hwArray: MutableList<HardwareFrameEntity>,
    arAugnmentedArray: ArrayList<DetectedAssetEntity>
): Session.SessionData? {

    /**
     *  double video_start_timestamp = 2;  // head
    double session_start_timestamp = 13; //head
    base_types.Matrix3x3 intrinsic_matrix = 3; // head
    uint32 motion_fps = 4; // head
    uint64 user_id = 10;
    string session_id = 11;

    string device_model = 14; // head
    string device_os = 15; // head
     */
    val session = Session.SessionData.newBuilder()
    session.sessionId = sessionId
    with(headFrames[0]) {
        session.videoStartTimestamp = videoStartTimeStamp!!.toDouble()
        session.sessionStartTimestamp = sessionStartTimestamp.toDouble()
        session.intrinsicMatrix = mapToMatrix3x3(cameraIntrinsics!!)
        session.motionFps = motionFps
        session.deviceModel = deviceModel
        session.deviceOs = androidVersion
    }

    session.addAllDetectedAssets(mapToDetectedAsset(arAugnmentedArray))
    session.addAllPlanes(mapToPlane(arPlainArray))
    session.addAllMotionFrames(mapToMotion(sensorFrameEntityArray))
    session.addAllDataFrames(mapToDataFrame(dataFrameArray))
    session.addAllHwFrames(mapToHardwareFrame(hwArray))
    return session.build()
}

fun mapToSessionDataHead(sessionHeadEntity: SessionHeadEntity): Proxy.SessionHeadData {
    val proxySessionFinalData = Proxy.SessionHeadData.newBuilder()
    with(sessionHeadEntity) {
        return proxySessionFinalData
            .setDeviceOs(androidVersion)
            .setDeviceModel(deviceModel)
            .setIntrinsicMatrix(mapToMatrix3x3(cameraIntrinsics))
            .setMotionFps(motionFps)
            .setSessionStartTimestamp(sessionStartTimestamp.toDouble())
            .setVideoStartTimestamp(videoStartTimeStamp.toDouble())
            .setFrameIndex(sessionHeadEntityCounter++)
            .build()
    }
}

fun mapToSessionDataFinal(finalData: SessionFinalEntity): Proxy.SessionFinalData? {
    sessionFinalEntityCounter++
    return Proxy.SessionFinalData.newBuilder()
        .setSessionEndTimestamp(finalData.sessionEndTimestamp.toDouble())
        .build()
}


fun mapToTimeStamp(frameTime: FrameTime): Double = frameTime.startSeconds.toDouble()
