/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.mediapipe.examples.handlandmarker

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.google.mediapipe.examples.handlandmarker.utils.UtilityHelper
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min


class OverlayView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    private var results: HandLandmarkerResult? = null
    private var linePaint = Paint()
    private var pointPaint = Paint()

    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1


    private lateinit var finger1a: PointF
    private lateinit var finger1b: PointF
    private lateinit var finger2a: PointF
    private lateinit var finger2b: PointF
    private lateinit var finger3a: PointF
    private lateinit var finger3b: PointF
    private lateinit var finger4a: PointF
    private lateinit var finger4b: PointF
    private lateinit var thumb: PointF
    private lateinit var thumb2: PointF

    init {
        initPaints()
    }

    fun clear() {
        results = null
        linePaint.reset()
        pointPaint.reset()
        invalidate()
        initPaints()
    }

    private fun initPaints() {
        linePaint.color =
            ContextCompat.getColor(context!!, R.color.mp_color_primary)
        linePaint.strokeWidth = LANDMARK_STROKE_WIDTH
        linePaint.style = Paint.Style.STROKE

        pointPaint.color = Color.YELLOW
        pointPaint.strokeWidth = LANDMARK_STROKE_WIDTH
        pointPaint.style = Paint.Style.FILL
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        results?.let { handLandmarkerResult ->
            println("handLandmarkerResult ${handLandmarkerResult.worldLandmarks()}  ")
            for (landmark in handLandmarkerResult.landmarks()) {
                var index = 0
                for (normalizedLandmark in landmark) {
                    index += 1
                    val localX = normalizedLandmark.x() * imageWidth * scaleFactor
                    val localY = normalizedLandmark.y() * imageHeight * scaleFactor

                    val angle = when (index) {
                        4 -> {
                            thumb = PointF(localX, localY)
                            0F
                        }

                        5 -> {
                            thumb2 = PointF(localX, localY)
                            UtilityHelper.getAngle(thumb, thumb2)
                        }

                        8 -> {
                            finger1a = PointF(localX, localY)
                            0F
                        }

                        9 -> {
                            finger1b = PointF(localX, localY)
                            UtilityHelper.getAngle(finger1a, finger1b)
                        }

                        12 -> {
                            finger2a = PointF(localX, localY)
                            0F
                        }

                        13 -> {
                            finger2b = PointF(localX, localY)
                            UtilityHelper.getAngle(finger2a, finger2b)
                        }

                        16 -> {
                            finger3a = PointF(localX, localY)
                            0F
                        }

                        17 -> {
                            finger3b = PointF(localX, localY)
                            UtilityHelper.getAngle(finger3a, finger3b)
                        }

                        landmark.size - 1 -> {
                            finger4a = PointF(localX, localY)
                            0F
                        }

                        landmark.size -> {
                            finger4b = PointF(localX, localY)
                            UtilityHelper.getAngle(finger4a, finger4b)
                        }

                        else -> {
                            0f
                        }
                    }


                    if (index == 5 || index == 9 || index == 13 || index == 17 || index == landmark.size) {
                        try {
                            val res: Resources = resources
                            val bitmap = BitmapFactory.decodeResource(res, R.drawable.nailpaint)
                            val matrix = Matrix()
                            matrix.preRotate(angle)
                            println(" Angle for nail "+angle)
                            val bmResult = Bitmap.createBitmap(
                                bitmap,
                                0,
                                0,
                                ceil(400 * abs(normalizedLandmark.z())).toInt(),
                                ceil(500 * abs(normalizedLandmark.z())).toInt(),
                                matrix,
                                true
                            )
                            canvas.drawBitmap(
                                bmResult,
                                (localX - (bmResult.width / 2)),
                                (localY - (bmResult.height / 2)),
                                null
                            )
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                        //println(" depth " + normalizedLandmark.z())

                        /*val vectorDrawable = ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.nail,
                            null
                        )
                        vectorDrawable?.setBounds(
                            (localX - 40).toInt(),
                            (localY - 70).toInt(),
                            (localX + 40).toInt(),
                            (localY + 70).toInt()
                        )
                        vectorDrawable?.draw(canvas)*/
                    } /*else {
                        pointPaint.color = Color.YELLOW
                        canvas.drawPoint(
                            localX,
                            localY,
                            pointPaint
                        )
                    }*/

                    //println("points     $localX    $localY   final  $finalX   $finalY")
                }

               /* HandLandmarker.HAND_CONNECTIONS.forEach {
                    canvas.drawLine(
                        landmark[it!!.start()]
                            .x() * imageWidth * scaleFactor,
                        landmark[it.start()]
                            .y() * imageHeight * scaleFactor,
                        landmark[it.end()]
                            .x() * imageWidth * scaleFactor,
                        landmark[it.end()]
                            .y() * imageHeight * scaleFactor,
                        linePaint
                    )
                }*/
            }
        }
    }

    fun setResults(
        handLandMarkerResults: HandLandmarkerResult,
        imageHeight: Int,
        imageWidth: Int,
        runningMode: RunningMode = RunningMode.IMAGE
    ) {
        results = handLandMarkerResults

        this.imageHeight = imageHeight
        this.imageWidth = imageWidth

        scaleFactor = when (runningMode) {
            RunningMode.IMAGE,
            RunningMode.VIDEO -> {
                min(width * 1f / imageWidth, height * 1f / imageHeight)
            }

            RunningMode.LIVE_STREAM -> {
                // PreviewView is in FILL_START mode. So we need to scale up the
                // landmarks to match with the size that the captured images will be
                // displayed.
                max(width * 1f / imageWidth, height * 1f / imageHeight)
            }
        }
        invalidate()
    }

    companion object {
        private const val LANDMARK_STROKE_WIDTH = 8F
    }
}
