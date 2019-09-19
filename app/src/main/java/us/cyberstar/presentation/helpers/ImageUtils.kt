package us.cyberstar.presentation.helpers

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import java.util.*


fun getBitmapFromView(view: View): Bitmap {
    //Define a bitmap with the same size as the view
    val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    //Bind a canvas to it
    val canvas = Canvas(returnedBitmap)
    //Get the view's background
    val bgDrawable = view.background
    if (bgDrawable != null)
    //has background drawable, then draw it on the canvas
        bgDrawable.draw(canvas)
    else
    //does not have background drawable, then draw white background on the canvas
        canvas.drawColor(Color.TRANSPARENT)
    // draw the view on the canvas
    view.draw(canvas)
    //return the bitmap
    return returnedBitmap
}

/**
 * Get bitmap of a view
 *
 * @param view source view
 * @return generated bitmap object
 */
fun getBitmapFromView1(view: View): Bitmap {
    view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
    val bitmap = Bitmap.createBitmap(
        view.measuredWidth, view.measuredHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    view.layout(0, 0, view.width, view.height)
    view.draw(canvas)
    return bitmap
}

/**
 * Stitch two images one below another
 *
 * @param listOfBitmapsToStitch List of bitmaps to stitch
 * @return resulting stitched bitmap
 */
fun combineImages(listOfBitmapsToStitch: ArrayList<Bitmap>): Bitmap {
    var bitmapResult: Bitmap? = null

    var width = 0
    var height = 0

    for (bitmap in listOfBitmapsToStitch) {
        width = Math.max(width, bitmap.width)
        height = height + bitmap.height
    }

    bitmapResult = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    val comboImageCanvas = Canvas(bitmapResult!!)

    var currentHeight = 0
    for (bitmap in listOfBitmapsToStitch) {
        comboImageCanvas.drawBitmap(bitmap, 0f, currentHeight.toFloat(), null)
        currentHeight = currentHeight + bitmap.height
    }

    return bitmapResult
}

