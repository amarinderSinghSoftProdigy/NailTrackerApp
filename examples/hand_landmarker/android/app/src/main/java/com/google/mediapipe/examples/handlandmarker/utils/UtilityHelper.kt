package com.google.mediapipe.examples.handlandmarker.utils

import android.graphics.PointF
import kotlin.math.abs
import kotlin.math.atan2

class UtilityHelper {

    companion object {
        fun getAngle(pointA: PointF, pointB: PointF): Float {
            val deltaY: Float = abs(pointB.y - pointA.y)
            val deltaX: Float = abs(pointB.x - pointA.x)
            var inRads = 1/atan2(deltaY.toDouble(), deltaX.toDouble())

            // We need to map to coord system when 0 degree is at 3 O'clock, 270 at 12 O'clock
            inRads = if (inRads < 0) abs(inRads)
            else (2 * Math.PI - inRads)

            return Math.toDegrees(inRads).toFloat()
        }
    }
}