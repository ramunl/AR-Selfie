package us.cyberstar.data.mapper.utils

import android.graphics.Color
import base_types.BaseTypes
import us.cyberstar.common.utils.RGB


fun mapToRGB(pixel: RGB): BaseTypes.RgbColor? {

    return BaseTypes.RgbColor.newBuilder()
        .setR(pixel.r)
        .setG(pixel.g)
        .setB(pixel.b)
        .build()
}

fun mapToRGB(pixel: Int): BaseTypes.RgbColor? {
    return BaseTypes.RgbColor.newBuilder()
        .setR(Color.red(pixel) / 255f)
        .setG(Color.green(pixel) / 255f)
        .setB(Color.blue(pixel) / 255f)
        .build()
}
