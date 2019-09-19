package us.cyberstar.data.entity.telemetry.base

import us.cyberstar.data.entity.telemetry.*


/**
 * If we want to save entity to file/server
 * we inherit it with this interface
 */
open class ArEntityTelemetry {
    companion object {
        var sessionFinalEntityCounter = 0
        var sessionHeadEntityCounter = 0
        var hardwareFrameEntityCounter = 0
        var detectedAssetCounter = 0
        var dataFrameEntityCounter = 0
        var arPlaneEntityCounter = 0
        var sensorFrameEntityCounter = 0

        fun resetCounter() {
            sessionFinalEntityCounter = 0
            sessionHeadEntityCounter = 0
            detectedAssetCounter = 0
            hardwareFrameEntityCounter = 0
            dataFrameEntityCounter = 0
            arPlaneEntityCounter = 0
            sensorFrameEntityCounter = 0
        }
    }

}