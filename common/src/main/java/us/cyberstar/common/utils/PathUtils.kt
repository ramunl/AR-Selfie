package us.cyberstar.common.utils

import android.os.Environment
import timber.log.Timber


fun generateDirUnique(sessionId: String): String {
    val dirUnique = getCacheFilesPath() + "${sessionId.substring(sessionId.lastIndexOf("-")+1)}/"
    Timber.e("-------------generated dirUnique = $dirUnique---------------")
    return dirUnique
}

fun generateFileNameUnique(sessionId: String, fileName_base: String): String {
    return generateDirUnique(sessionId) + fileName_base
}


fun getCacheFilesPath() = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/CyberStar/"
