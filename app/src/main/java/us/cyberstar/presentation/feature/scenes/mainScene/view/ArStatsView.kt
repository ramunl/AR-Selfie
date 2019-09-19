package us.cyberstar.presentation.feature.scenes.mainScene.view

import android.graphics.Bitmap

interface ArStatsView {
    fun updateFramesNum(framesNum: Int)
    fun updateArFramesNum(arFramesNum: Int)
    fun updateFPSNum(fps: Int)
    fun updateArPlanesNum(planesNum: Int)
    fun updateTimer(timeStamp: Long)
    fun updateFrame(bmp: Bitmap)
}