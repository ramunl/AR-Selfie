package us.cyberstar.data

import interactive.ModelRegistryServiceGrpc
import proxy.ARServiceGrpc
import proxy.TelemetryServiceGrpc
import social.ARMSocialServiceGrpc

interface ProtoServiceProvider {
    val modelRegistryService: ModelRegistryServiceGrpc.ModelRegistryServiceStub?
    var armSocialServiceGrpc: ARMSocialServiceGrpc.ARMSocialServiceStub?
    var arServiceBlocking: ARServiceGrpc.ARServiceBlockingStub?
    var arService: ARServiceGrpc.ARServiceStub?
    var telemetryServiceBlocking: TelemetryServiceGrpc.TelemetryServiceBlockingStub?
    var telemetryService: TelemetryServiceGrpc.TelemetryServiceStub?
    fun shutDownChannel()
}