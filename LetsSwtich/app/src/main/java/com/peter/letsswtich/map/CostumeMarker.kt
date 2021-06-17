package com.peter.letsswtich.map

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.peter.letsswtich.R
import de.hdodenhof.circleimageview.CircleImageView


fun createCustomMarker(context: Context,  resource: Bitmap, _name: String?): Bitmap? {
    val marker: View =
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
            R.layout.circular_image,
            null
        )
    val markerImage = marker.findViewById(R.id.user_dp) as CircleImageView
    markerImage.setImageBitmap(resource)
    val displayMetrics = DisplayMetrics()
    (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
    marker.setLayoutParams(ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT))
    marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
    marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
    marker.buildDrawingCache()
    val bitmap = Bitmap.createBitmap(
        marker.getMeasuredWidth(),
        marker.getMeasuredHeight(),
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    marker.draw(canvas)
    return bitmap
}