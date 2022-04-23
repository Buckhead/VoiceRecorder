package com.buckheadkorea.pavlov.client.audiorecorder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

class WaveformView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var paint = Paint()
    private var amplitudes = ArrayList<Float>()
    private var spikes = ArrayList<RectF>()

    private var radius = 6f
    private var w = 9f
    private var d = 6f

    private var sw = 0f
    private var sh = 400f

    private var maxSpikes = 0

    init {
        paint.color = Color.rgb(244,81,30)
        sw = resources.displayMetrics.widthPixels.toFloat()

        maxSpikes = (sw/(w+d)).toInt()
    }

    fun addAmplitude(amp:Float){
        var norm = Math.min(amp.toInt()/7, 400).toFloat()
        amplitudes.add(norm)

//        var left = 0f
//        var top = 0f
//
//        var right = left +w
//        var bottom = amp
//
//        spikes.add(RectF(left,top, right, bottom))
//        invalidate()
        spikes.clear()
        var amps = amplitudes.takeLast((maxSpikes))
        for (i in amps.indices){
            var left = sw -i*(w+d)
            var top = sh/2 - amps[i]/2
            var right = left+w
            var bottom = sh/2+amps[i]/2

            spikes.add(RectF(left, top, right, bottom))
            invalidate()
        }

    }

    fun clear(): ArrayList<Float>{
        var amps = amplitudes.clone() as ArrayList<Float>
        amplitudes.clear()
        spikes.clear()
        invalidate()

        return amps
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        spikes.forEach{
            canvas?.drawRoundRect(it,radius, radius,paint)
        }
//        canvas?.drawRoundRect(RectF(20f,30f,20+30f,30f+60f),6f, 6f, paint)
    }
}