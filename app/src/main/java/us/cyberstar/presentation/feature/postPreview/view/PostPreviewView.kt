package us.cyberstar.presentation.feature.postPreview.view

import android.graphics.Bitmap
import com.arellomobile.mvp.MvpView

interface PostPreviewView : MvpView {
    fun showPhotoPreview(lastBitmap: Bitmap?)
}