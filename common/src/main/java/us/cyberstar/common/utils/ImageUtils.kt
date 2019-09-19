package us.cyberstar.common.utils

import android.graphics.*
import android.media.Image
import us.cyberstar.common.utils.yuvNv21Utils.decodeNV21Pixel
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer


val QUALITY = 60

fun planesToByteArray(planes: Array<out Image.Plane>, width: Int, height: Int): Bitmap? {
    var data: ByteArray? = null

    data = NV21toJPEG(
        YUV_420_888toNV21(planes),
        width, height
    )

    val boundOption = BitmapFactory.Options()
    boundOption.outHeight = height
    boundOption.outWidth = width

    return BitmapFactory.decodeByteArray(data, 0, data.size, boundOption)
}

var nv21 = ByteArray(0)

fun YUV_420_888toNV21(planes: Array<out Image.Plane>): ByteArray {
    //val nv21: ByteArray
    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()
    nv21 = ByteArray(ySize + uSize + vSize)
    //nv21 = ByteArray(0)
    //U and V are swapped
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    return nv21
}

fun jpegToBitmap(image: Image, width: Int, height: Int): Bitmap? {
    var data: ByteArray? = null

    data = NV21toJPEG(
        YUV_420_888toNV21(image),
        image.width, image.height
    )

    val boundOption = BitmapFactory.Options()
    boundOption.outHeight = height
    boundOption.outWidth = width

    return BitmapFactory.decodeByteArray(data, 0, data.size, boundOption)
}

fun imageToBitmap(image: Image): Bitmap? {
    val planes = image.planes
    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer
    val buff = mutableListOf(yBuffer, uBuffer, vBuffer)
    return jpegToBitmap(
        buff,
        image.width,
        image.height
    )
}

fun YUV_420_888toNV21(image: Image): ByteArray {

    val yBuffer = image.planes[0].buffer
    val uBuffer = image.planes[1].buffer
    val vBuffer = image.planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    nv21 = ByteArray(ySize + uSize + vSize)

    //U and V are swapped
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    return nv21
}


data class ImageCopy(
    val yBuffer: ByteBuffer,
    val uBuffer: ByteBuffer,
    val vBuffer: ByteBuffer,
    val width: Int,
    val height: Int
)

fun YUV_420_888toNV21(image: ImageCopy): ByteArray {


    val ySize = image.yBuffer.remaining()
    val uSize = image.uBuffer.remaining()
    val vSize = image.vBuffer.remaining()

    nv21 = ByteArray(ySize + uSize + vSize)

    //U and V are swapped
    image.yBuffer.get(nv21, 0, ySize)
    image.vBuffer.get(nv21, ySize, vSize)
    image.uBuffer.get(nv21, ySize + vSize, uSize)

    return nv21
}

fun imageToJPEG(
    imageTemp: ImageCopy
): ByteArray? {
    with(imageTemp) {
        val buff = mutableListOf(yBuffer, uBuffer, vBuffer)
        return imageToJPEG(
            buff,
            width,
            height
        )
    }
}

fun imageCopy(image: Image): ImageCopy {
    val planes = image.planes
    val yBuffer = deepCopy(planes[0].buffer)
    val uBuffer = deepCopy(planes[1].buffer)
    val vBuffer = deepCopy(planes[2].buffer)
    return ImageCopy(yBuffer, uBuffer, vBuffer, image.width, image.height)
}

private fun getSerializedSize(image: Image): Int {
    var size = 0

    for (plane in image.planes) {
        size += plane.buffer.capacity()
    }

    return size
}


fun Image.asYUV(): ByteArray {
    var planes: Array<Image.Plane> = planes
    // NV21 expects planes in order YVU, not YUV:
   // if (image.format == ImageFormat.YUV_420_888)
   // planes = arrayOf(planes[0], planes[2], planes[1])
    planes = arrayOf(planes[0], planes[1], planes[2])
    val serializeBytes = ByteArray(getSerializedSize(this))
    var nextFree = 0
    for (plane in planes) {
        val buffer = plane.buffer
        buffer.position(0)
        val nBytes = buffer.remaining()
        plane.buffer.get(serializeBytes, nextFree, nBytes)
        nextFree += nBytes
    }
    return serializeBytes
}

fun deepCopy(source: ByteBuffer): ByteBuffer {
    val target = ByteBuffer.allocate(source.remaining())
    val sourceP = source.position()
    val sourceL = source.limit()
    target.put(source)
    target.flip()
    source.position(sourceP)
    source.limit(sourceL)
    return target
}

/*
fun rotateNV21(input: ByteArray, width: Int, height: Int, rotation: Int): ByteArray {
    val output = mutableListOf<Byte>()
    val swap = rotation == 90 || rotation == 270
    val yflip = rotation == 90 || rotation == 180
    val xflip = rotation == 270 || rotation == 180
    for (x in 0 until width) {
        for (y in 0 until height) {
            var xo = x
            var yo = y
            var w = width
            var h = height
            var xi = xo
            var yi = yo
            xi = w * yo / h
            yi = h * xo / w
            yi = h - yi - 1
            xi = w - xi - 1
            output[w * yo + xo] = input[w * yi + xi]
            val fs = w * h
            val qs = fs shr 2
            xi = xi shr 1
            yi = yi shr 1
            xo = xo shr 1
            yo = yo shr 1
            w = w shr 1
            h = h shr 1
            val ui = fs + (w * yi + xi) * 2
            val uo = fs + (w * yo + xo) * 2
            val vi = ui + 1
            val vo = uo + 1
            output[uo] = input[ui]
            output[vo] = input[vi]
        }
    }
    return output.toByteArray()
}*/


private fun NV21toJPEG(nv21: ByteArray, width: Int, height: Int): ByteArray {
    val out = ByteArrayOutputStream()
    val yuv = YuvImage(nv21, ImageFormat.NV21, width, height, null)
    yuv.compressToJpeg(Rect(0, 0, width, height), QUALITY, out)
    return out.toByteArray()
}


fun ByteArray.toBitmap() = BitmapFactory.decodeByteArray(this, 0, this.size)

fun Bitmap.asJpeg(): ByteArray? {
    val os = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, QUALITY, os)
    return os.toByteArray()
}


fun imageToJPEG(image: Image): ByteArray? {
    val planes = image.planes
    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer
    val buff = mutableListOf(yBuffer, uBuffer, vBuffer)
    return imageToJPEG(
        buff,
        image.width,
        image.height
    )
}


fun imageToJPEG(buffers: List<ByteBuffer>, width: Int, height: Int): ByteArray? {
    var data: ByteArray? = null
    data = NV21toJPEG(
        YUV_420_888toNV21(buffers),
        width, height
    )
    return data
}

fun fileToBitmap(filePath: String) = BitmapFactory.decodeFile(filePath)


fun jpegToBitmap(buffers: List<ByteBuffer>, width: Int, height: Int): Bitmap? {
    val data: ByteArray? = imageToJPEG(buffers, width, height)
    val boundOption = BitmapFactory.Options()
    boundOption.outHeight = height
    boundOption.outWidth = width
    return BitmapFactory.decodeByteArray(data, 0, data!!.size, boundOption)
}


fun jpegToBitmap(data: ByteArray?, width: Int, height: Int): Bitmap? {
    val boundOption = BitmapFactory.Options()
    boundOption.outHeight = height
    boundOption.outWidth = width
    return BitmapFactory.decodeByteArray(data, 0, data!!.size, boundOption)
}

private fun YUV_420_888toNV21(planes: List<ByteBuffer>): ByteArray {
    val nv21: ByteArray

    val yBuffer = planes[0]
    val uBuffer = planes[1]
    val vBuffer = planes[2]

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    nv21 = ByteArray(ySize + uSize + vSize)

    //U and V are swapped
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    return nv21
}
/*
fun decodeNV21Pixel(nv21: ByteArray, width: Int, height: Int, x: Int, y: Int): Int {
    try {
        val lColStride = 1
        val cColStride = 2
        val Y = 0xff and nv21[y * width + x * lColStride].toInt()
        val Cr = (0xff and nv21[height * width + y / 2 * width + x / 2 * cColStride].toInt()) - 128
        val Cb = (0xff and nv21[height * width + y / 2 * width + x / 2 * cColStride + 1].toInt()) - 128
        return (-0x1000000
                or (Math.max(Math.min(Y + Cr + (Cr shr 1) + (Cr shr 2) + (Cr shr 6), 255), 0) shl 16)
                or (Math.max(
            Math.min(
                Y - (Cr shr 2) + (Cr shr 4) + (Cr shr 5) - (Cb shr 1) + (Cb shr 3) + (Cb shr 4) + (Cb shr 5),
                255
            ), 0
        ) shl 8)
                or Math.max(Math.min(Y + Cb + (Cb shr 2) + (Cb shr 3) + (Cb shr 5), 255), 0))
    } catch (e: Exception) {
        Crashlytics.logException(e)
        //Timber.d("$e,  w = $width, h = $height x =$x, y = $y")
    }
    return 0
}*/

/*fun getPixel(image: Image, x: Int, y: Int): RGB {
    var nv21 = YUV_420_888toNV21(image)
    return getPixel(nv21, image.width, image.height, x, y)
}*/

fun getPixel(nv21: ByteArray, width: Int, height: Int, x: Int, y: Int): RGB {
    val pixel = decodeNV21Pixel(nv21, width, height, x, y)
    val color255 = 255f
    val red = Color.red(pixel) / color255
    val green = Color.green(pixel) / color255
    val blue = Color.blue(pixel) / color255
    return RGB(red, green, blue)
}

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

/**
 * Convert bitmap to byte array using ByteBuffer.
 */
fun Bitmap.convertToByteArray(): ByteArray {
    //minimum number of bytes that can be used to store this bitmap's pixels
    val size = this.byteCount

    //allocate new instances which will hold bitmap
    val buffer = ByteBuffer.allocate(size)
    val bytes = ByteArray(size)

    //copy the bitmap's pixels into the specified buffer
    this.copyPixelsToBuffer(buffer)

    //rewinds buffer (buffer position is set to zero and the mark is discarded)
    buffer.rewind()

    //transfer bytes from buffer into the given destination array
    buffer.get(bytes)

    //return bitmap's pixels
    return bytes
}

data class RGB(val r: Float, val g: Float, val b: Float)