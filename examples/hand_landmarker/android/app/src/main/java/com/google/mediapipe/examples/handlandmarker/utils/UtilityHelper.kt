package com.google.mediapipe.examples.handlandmarker.utils

import android.graphics.Path
import android.graphics.PointF
import android.util.Log
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

class UtilityHelper {

    companion object {

        fun getAngle(pointA: PointF, pointB: PointF): Double {
            val deltaY: Float = abs(pointB.y - pointA.y)
            val deltaX: Float = abs(pointB.x - pointA.x)
            Log.e("", " point 1 $pointA point 2 $pointB")
            var inRads = atan2(deltaY.toDouble(), deltaX.toDouble())
            // We need to map to coord system when 0 degree is at 3 O'clock, 270 at 12 O'clock
            inRads = if (inRads < 0) abs(inRads)
            else (2 * Math.PI - inRads)
            return (360 - (inRads * 180 / Math.PI))
        }


        fun getPath(thumbNail: Path, localX: Float, localY: Float): Path {
            val x = 40
            thumbNail.reset()
            thumbNail.moveTo(localX - x, localY - x)
            thumbNail.lineTo(localX - x, localY)
            thumbNail.quadTo(localX, localY + (x + x + x), localX + x, localY)
            thumbNail.lineTo(localX + x, localY - x)
            thumbNail.quadTo(localX, localY - (x + x), localX - x, localY - x)
            thumbNail.close()
            return thumbNail
        }

        fun getTiltedPath(index: Int, thumbNail: Path, point1: PointF, point2: PointF): Path {
            val x = if (index < 6) 25 else 60
            thumbNail.reset()
            if (point1.x > point2.x && point1.y < point2.y) {
                Log.e("ar"," Tilted right")
                thumbNail.moveTo(point1.x, point1.y - x)
                thumbNail.quadTo(point2.x, point2.y, point1.x + x, point1.y)
                thumbNail.quadTo(point1.x + x, point1.y - x, point1.x, point1.y - x)
            } else if (point1.x < point2.x && point1.y < point2.y) {
                Log.e("ar"," Tilted left")
                thumbNail.moveTo(point1.x - x, point1.y)
                thumbNail.quadTo(point2.x, point2.y, point1.x + x, point1.y - x)
                thumbNail.quadTo(
                    point1.x - x,
                    point1.y - x/2,
                    point1.x - x,
                    point1.y
                )
            } else {
                Log.e("ar"," Tilted forward")
                thumbNail.moveTo(point1.x - x, point1.y - x)
                thumbNail.quadTo(point2.x, point2.y, point1.x + x, point1.y - x)
                thumbNail.quadTo(point1.x, point1.y - (2 * x), point1.x - x, point1.y - x)
            }
            thumbNail.close()
            return thumbNail
        }


        fun getCenterPoint(pointA: PointF, pointB: PointF): PointF {
            val x = (pointA.x + pointB.x) / 2
            val y = (pointA.y + pointB.y) / 2
            return PointF(x, y)
        }

        fun getLength(pointA: PointF, pointB: PointF): Float {
            val x = (pointB.x - pointA.x) * (pointB.x - pointA.x)
            val y = (pointB.y - pointA.y) * (pointB.y - pointA.y)
            return sqrt(x + y)
        }

        fun perpendicularFormula(pointA: PointF, pointB: PointF): PointF {
            val d =
                ((pointA.y - pointB.y) * (pointA.y - pointB.y)) + ((pointB.x - pointA.x) + (pointB.x - pointA.x))
            val x = ((pointA.x + pointB.x) / 2 + (pointA.y - pointB.y) / sqrt(d))
            val y = ((pointA.y + pointB.y) / 2 + (pointB.x - pointA.x) / sqrt(d))
            return PointF(x, y)
        }

    }

    data class FingerPoints(
        var finger1a: PointF,
        var finger1b: PointF,
        var finger2a: PointF,
        var finger2b: PointF,
        var finger3a: PointF,
        var finger3b: PointF,
        var finger4a: PointF,
        var finger4b: PointF,
        var thumb: PointF,
        var thumb2: PointF
    )
}