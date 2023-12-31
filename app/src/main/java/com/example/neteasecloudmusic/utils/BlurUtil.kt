package com.example.neteasecloudmusic.utils

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur

object BlurUtil {
    fun blur(context: Context, inputBitmap: Bitmap, radius: Float): Bitmap {
        val renderScript = RenderScript.create(context)
        val input = Allocation.createFromBitmap(renderScript, inputBitmap)
        val output = Allocation.createTyped(renderScript, input.type)
        val scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        scriptIntrinsicBlur.setInput(input)
        scriptIntrinsicBlur.setRadius(radius)
        scriptIntrinsicBlur.forEach(output)
        output.copyTo(inputBitmap)
        renderScript.destroy()
        return inputBitmap
    }
}