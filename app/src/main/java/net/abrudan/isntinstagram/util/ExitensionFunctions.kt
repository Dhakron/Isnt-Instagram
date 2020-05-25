package net.abrudan.isntinstagram.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import net.abrudan.isntinstagram.R
import java.nio.ByteBuffer
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

fun Bitmap.flip():Bitmap{
    val matrix = Matrix()
    matrix.preScale(-1.0f, 1.0f)
    return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
}
fun Bitmap.convertToByteArray(): ByteArray = ByteBuffer.allocate(byteCount).apply {
    copyPixelsToBuffer(this)
    rewind()
}.array()

fun Long.getTimeDiff(context: Context):String{
    val diff=Date().time-this
    val diffInDays: Long = TimeUnit.MILLISECONDS.toDays(diff)
    val diffInHours: Long = TimeUnit.MILLISECONDS.toHours(diff)
    val diffInMin: Long = TimeUnit.MILLISECONDS.toMinutes(diff)
    val diffInSec: Long = TimeUnit.MILLISECONDS.toSeconds(diff)
    var date=context.getString(R.string.tv_datePost_front)+" "
    if(diffInDays>=1){
        if(diffInDays==1L)date+=diffInDays.toString()+" "+context.getString(R.string.tv_datePost_end_day)
        else date+=diffInDays.toString()+" "+context.getString(R.string.tv_datePost_end_days)
    }else if(diffInHours>=1){
        if(diffInHours==1L)date+=diffInHours.toString()+" "+context.getString(R.string.tv_datePost_end_hour)
        else date+=diffInHours.toString()+" "+context.getString(R.string.tv_datePost_end_hours)
    }else if(diffInMin>=1){
        if(diffInMin==1L)date+=diffInMin.toString()+" "+context.getString(R.string.tv_datePost_end_minute)
        else date+=diffInMin.toString()+" "+context.getString(R.string.tv_datePost_end_minutes)
    }else if(diffInSec>=1){
        date+=diffInSec.toString()+" "+context.getString(R.string.tv_datePost_end_seconds)
    }
    return date
}