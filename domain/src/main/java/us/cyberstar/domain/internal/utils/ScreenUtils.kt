package us.cyberstar.domain.internal.utils

import android.content.Context
import android.graphics.Point


fun screenCenter(context: Context): Point = Point(getScrW(context) / 2, getScrH(context) / 2)


fun getScrW(context: Context) = context.resources.displayMetrics.widthPixels
fun getScrH(context: Context) = context.resources.displayMetrics.heightPixels