package us.cyberstar.data.mapper.utils

import base_types.BaseTypes
import com.google.ar.core.Pose


fun mapToPose(pose: Pose): BaseTypes.AndroidPose =
    BaseTypes.AndroidPose.newBuilder().setAndroidTranslation(mapToVectorFloat3(pose.translation))
        .setAndroidQuaternion(mapToVectorFloat4(pose.rotationQuaternion)).build()

fun mapToArPose(pose: BaseTypes.AndroidPose) = Pose(pose.androidTranslation.asArray(), pose.androidQuaternion.asArray())
