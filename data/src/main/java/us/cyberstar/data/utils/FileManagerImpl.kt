package us.cyberstar.data.utils

import com.google.protobuf.MessageLite
import timber.log.Timber
import us.cyberstar.common.utils.getCacheFilesPath
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


fun loadFromDisk(fileName: String): FileInputStream? {
    Timber.d("loadFromDisk")
    var res: FileInputStream? = null
    val outputFile = File(fileName)
    Timber.d("does outputFile $outputFile exists ?")
    if (outputFile.exists()) {
        res = FileInputStream(outputFile)
    }
    return res
}

fun saveToDisk(fileName: String, message: MessageLite): File {
    Timber.d("saveToDisk $fileName size = ${message.serializedSize}")
    val outputFile = recreateFile(fileName)
    FileOutputStream(outputFile).use { outputStream ->
        ByteArrayOutputStream().use { outputData ->
            message.writeTo(outputStream)
            outputData.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
        }
    }
    return outputFile
}

fun recreateFile(fileName: String): File {
    val outputFile = File(fileName)
    if (outputFile.exists()) {
        Timber.d("$outputFile exists..")
        outputFile.delete()
    }
    if (!outputFile.parentFile.exists()) {
        outputFile.parentFile.mkdirs()
    }
    val created = outputFile.createNewFile()
    Timber.d("created $fileName $created")
    return outputFile
}


fun saveToDisk(fileName: String, byteArray: ByteArray): File {
    Timber.d("saveToDisk $fileName size = ${byteArray.size}")
    val outputFile = recreateFile(fileName)
    outputFile.writeBytes(byteArray)
    return outputFile
}

