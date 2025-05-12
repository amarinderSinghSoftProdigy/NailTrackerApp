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

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.google.mediapipe.examples.handlandmarker.utils.UtilityHelper
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import kotlin.math.max
import kotlin.math.min


class OverlayView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    val nailUIViewModel: NailUIViewModel = NailUIViewModel((context as Activity).application)

    private var results: HandLandmarkerResult? = null
    private var linePaint = Paint()
    private var pointPaint = Paint()

    private var thumbNail: Path = Path()
    private var fing1: Path = Path()
    private var fing2: Path = Path()
    private var fing3: Path = Path()
    private var fing4: Path = Path()

    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1

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
        linePaint.style = Paint.Style.FILL

        pointPaint.color = Color.YELLOW
        pointPaint.strokeWidth = LANDMARK_STROKE_WIDTH
        pointPaint.style = Paint.Style.FILL
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        results?.let { handLandmarkResult ->
            for (landmark in handLandmarkResult.landmarks()) {
                var index = 0
                for (normalizedLandmark in landmark) {
                    index += 1
                    val localX = normalizedLandmark.x() * imageWidth * scaleFactor
                    val localY = normalizedLandmark.y() * imageHeight * scaleFactor
                    nailUIViewModel.onEvent(
                        NailUIEvent.SetFingerData(
                            index,
                            PointF(localX, localY)
                        )
                    )
                    if (index == 5 && nailUIViewModel.nailUiState.value.thumb != null &&
                        nailUIViewModel.nailUiState.value.thumb2 != null) {
                        canvas.drawPath(
                            UtilityHelper.getTiltedPath(
                                index,
                                thumbNail,
                                nailUIViewModel.nailUiState.value.thumb2!!,
                                UtilityHelper.getCenterPoint(
                                    nailUIViewModel.nailUiState.value.thumb2!!,
                                    nailUIViewModel.nailUiState.value.thumb!!
                                )
                            ), linePaint
                        )
                    } else if (index == 9 && nailUIViewModel.nailUiState.value.finger1a != null &&
                        nailUIViewModel.nailUiState.value.finger1b != null) {
                        canvas.drawPath(
                            UtilityHelper.getTiltedPath(
                                index,
                                fing1,
                                nailUIViewModel.nailUiState.value.finger1b!!,
                                UtilityHelper.getCenterPoint(
                                    nailUIViewModel.nailUiState.value.finger1a!!,
                                    nailUIViewModel.nailUiState.value.finger1b!!
                                )
                            ), linePaint
                        )
                    } else if (index == 13 && nailUIViewModel.nailUiState.value.finger2a != null &&
                        nailUIViewModel.nailUiState.value.finger2b != null) {
                        canvas.drawPath(
                            UtilityHelper.getTiltedPath(
                                index,
                                fing2,
                                nailUIViewModel.nailUiState.value.finger2b!!,
                                UtilityHelper.getCenterPoint(
                                    nailUIViewModel.nailUiState.value.finger2b!!,
                                    nailUIViewModel.nailUiState.value.finger2a!!
                                )
                            ), linePaint
                        )
                    } else if (index == 14 && nailUIViewModel.nailUiState.value.finger3a != null &&
                        nailUIViewModel.nailUiState.value.finger3b != null
                    ) {
                        canvas.drawPath(
                            UtilityHelper.getTiltedPath(
                                index,
                                fing3,
                                nailUIViewModel.nailUiState.value.finger3b!!,
                                UtilityHelper.getCenterPoint(
                                    nailUIViewModel.nailUiState.value.finger3b!!,
                                    nailUIViewModel.nailUiState.value.finger3a!!
                                )
                            ), linePaint
                        )
                    } else if (index == landmark.size && nailUIViewModel.nailUiState.value.finger4a != null &&
                        nailUIViewModel.nailUiState.value.finger4b != null) {
                        canvas.drawPath(
                            UtilityHelper.getTiltedPath(
                                index,
                                fing4,
                                nailUIViewModel.nailUiState.value.finger4b!!,
                                UtilityHelper.getCenterPoint(
                                    nailUIViewModel.nailUiState.value.finger4b!!,
                                    nailUIViewModel.nailUiState.value.finger4a!!
                                )
                            ), linePaint
                        )
                    }
                }
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
