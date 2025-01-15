package com.playlistmaker.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.widget.ImageView
import com.practicum.playlistmaker.R

object BitmapProcessor {
    fun loadImageWithRoundedCorners(
        filePath: String,
        imageView: ImageView,
        cornerRadius: Float,
        placeholderResId: Int = R.drawable.ic_cover_placeholder
    ) {
        val bitmap = BitmapFactory.decodeFile(filePath)
        if (bitmap != null) {
            val roundedBitmap = getRoundedCornerBitmap(bitmap, cornerRadius)
            imageView.setImageBitmap(roundedBitmap)
        } else {
            imageView.setImageResource(placeholderResId)
        }
    }

    private fun getRoundedCornerBitmap(bitmap: Bitmap, cornerRadius: Float): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint().apply { isAntiAlias = true }
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)

        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return output
    }
}
