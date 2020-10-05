package net.abrudan.isntinstagram.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import net.abrudan.isntinstagram.R
import java.nio.ByteBuffer
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.days

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
    val calendar =GregorianCalendar(TimeZone.getTimeZone("GMT"))
    calendar.timeInMillis=diff
    val diffInYears = (TimeUnit.MILLISECONDS.toDays(diff)/365)
    val diffInMonths = calendar.get(Calendar.MONTH)
    val diffInDays: Long = TimeUnit.MILLISECONDS.toDays(diff)
    val diffInHours: Long = TimeUnit.MILLISECONDS.toHours(diff)
    val diffInMin: Long = TimeUnit.MILLISECONDS.toMinutes(diff)
    val diffInSec: Long = TimeUnit.MILLISECONDS.toSeconds(diff)
    var date=context.getString(R.string.tv_datePost_front)+" "
    if(diffInYears>=1){
        if(diffInYears==1L)date+=diffInYears.toString()+" "+context.getString(R.string.tv_datePost_end_year)
        else date+=diffInYears.toString()+" "+context.getString(R.string.tv_datePost_end_years)
    }else if(diffInMonths>=1){
        if(diffInDays==1L)date+=diffInMonths.toString()+" "+context.getString(R.string.tv_datePost_end_month)
        else date+=diffInMonths.toString()+" "+context.getString(R.string.tv_datePost_end_months)
    }else if(diffInDays>=1){
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

fun Long.getTimeDiffForComments(context: Context):String{
    val diff=Date().time-this
    val calendar =GregorianCalendar(TimeZone.getTimeZone("GMT"))
    calendar.timeInMillis=diff
    val diffInYears = (TimeUnit.MILLISECONDS.toDays(diff)/365)
    val diffInMonths = calendar.get(Calendar.MONTH)
    val diffInDays: Long = TimeUnit.MILLISECONDS.toDays(diff)
    val diffInHours: Long = TimeUnit.MILLISECONDS.toHours(diff)
    val diffInMin: Long = TimeUnit.MILLISECONDS.toMinutes(diff)
    val diffInSec: Long = TimeUnit.MILLISECONDS.toSeconds(diff)
    var date=""
    if(diffInYears>=1){
        if(diffInYears==1L)date+=diffInYears.toString()+" "+context.getString(R.string.tv_datePost_end_year_comment)
        else date+=diffInYears.toString()+" "+context.getString(R.string.tv_datePost_end_years_comment)
    }else if(diffInMonths>=1){
        if(diffInDays==1L)date+=diffInMonths.toString()+" "+context.getString(R.string.tv_datePost_end_month_comment)
        else date+=diffInMonths.toString()+" "+context.getString(R.string.tv_datePost_end_months_comment)
    }else if(diffInDays>=1){
        date+=diffInDays.toString()+" "+context.getString(R.string.tv_datePost_end_days_comment)
    }else if(diffInHours>=1){
        date+=diffInHours.toString()+" "+context.getString(R.string.tv_datePost_end_hours_comment)
    }else if(diffInMin>=1){
        date+=diffInMin.toString()+" "+context.getString(R.string.tv_datePost_end_minutes_comment)
    }else if(diffInSec>=1){
        date+=diffInSec.toString()+" "+context.getString(R.string.tv_datePost_end_seconds_comment)
    }
    return date
}

fun prueba(){

}