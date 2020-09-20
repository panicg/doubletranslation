package com.kevin.common

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Matrix
import android.view.View
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object ConvertUtil {
    private val gson: Gson = GsonBuilder().disableHtmlEscaping().create()

    fun writeObjectToString(data: Any): String {
        return gson.toJson(data)
    }

    fun <T> parseObjectFromString(str : String, t : Type) : T{
        return gson.fromJson(str, t)
    }

    fun <E> getListType(): Type {
        return object : TypeToken<List<E>>() {

        }.type
    }

    fun <E> getArrayType() : Type {
        return object : TypeToken<Array<E>>() {

        }.type
    }

    fun <K, V> getMapType(): Type {
        return object : TypeToken<Map<K, V>>() {

        }.type
    }
}


fun Date.previousDay(count: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(Calendar.DATE, -count)

    return cal.time
}

fun Date.afterDay(count: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(Calendar.DATE, count)

    return cal.time
}

fun Date.convertMillis(): Long {
    val cal = Calendar.getInstance()
    cal.time = this
    return cal.timeInMillis
}

fun Date.startOfDay(): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.HOUR, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.time
}
fun Date.endOfDay() : Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.set(Calendar.SECOND, 59)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MILLISECOND, 999)
    return cal.time
}

fun Date.startOfYear(dayCount : Int){
    val cal = Calendar.getInstance()

}




open class DateFormatter {
    companion object{
        val dateformatAPI: String
            get() = "yyyy-MM-dd"

        val dateformatSimple: String
            get() = "yyyyMMdd"

        val dateFormatShort: String
            get() = "E, dd MMM"

        val dateformatTrip: String
            get() = "MM/dd/yyyy"

        val dateformatLong: String
            get() = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    }
}




fun Date.convertString(strFormat: String, isUTC : Boolean = false): String {

    val dateFormat = SimpleDateFormat(strFormat)
    if (isUTC){
        dateFormat.timeZone = SimpleTimeZone(0, "UTC")
    }
    return dateFormat.format(this)
}

fun Date.convertStringformatShort(isUTC: Boolean = false) : String{
    return convertString("E, dd MMM", isUTC)
}

fun Date.convertStringformatShortWithTime(isUTC : Boolean = false) : String{
    return convertString("E, dd MMM / HH:mm", isUTC)
}

fun String.convertDate(isUTC : Boolean = false) : Date? {
    return when {
        this.length == 23 -> { convertDate("yyyy-MM-dd HH:mm:ss.SSS", isUTC)}
        this.length > 23 -> {convertDate("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", isUTC)}
        else -> {convertDate("yyyy-MM-dd HH:mm:ss", isUTC)}
    }
}

fun String.convertDate(strFormat : String, isUTC : Boolean = false) : Date? {
    val dateFormat = SimpleDateFormat(strFormat)
    dateFormat.timeZone = if (isUTC) TimeZone.getTimeZone("UTC") else TimeZone.getDefault()
    return dateFormat.parse(this)
}

fun convertDate(year : Int, month : Int, dayOfMonth : Int) : Date{
    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, year)
    cal.set(Calendar.MONTH, month)
    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
    return cal.time
}

fun String.convertDate(): Date? {
    val strFormat = when (this.trim().count()) {
        6 -> {
            "yyMMdd"
        }
        8 -> {
            "yyyyMMdd"
        }
        10 -> {
            "yyyy-MM-dd"
        }
        12 -> {
            "yyMMddHHmmss"
        }
        14 -> {
            if (this.contains("  ")) {
                "yyMMdd  HHmmss"
            } else {
                "yyyyMMddHHmmss"
            }
        }
        else -> "yyMMdd"
    }
    return convertDate(strFormat)
}

fun String.convertDate(strFormat: String): Date? {
    return try {
        val dateFormat = SimpleDateFormat(strFormat)
        dateFormat.parse(this)
    } catch (e: Exception) {
        null
    }
}


inline fun <T : Any> guardLet(vararg elements: T?, closure: () -> Nothing): List<T> {
    return if (elements.all { it != null }) {
        elements.filterNotNull()
    } else {
        closure()
    }
}

inline fun <T : Any> ifLet(vararg elements: T?, closure: (List<T>) -> Unit) {
    if (elements.all { it != null }) {
        closure(elements.filterNotNull())
    }
}

val Number.convertCurrency: String
    get() {
        val format = DecimalFormat("###,###.##")
        return format.format(this)
    }

/**
 * Date picker
 */
fun DatePicker.updateDate(date : Date){
    val cal = Calendar.getInstance()
    cal.time = date

    this.updateDate(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
}

/**
 * 알럿
 */
fun Fragment.alert(title : String = "", message : String = "", isChoice : Boolean = false, isCancelable : Boolean = false, completion : ((isConfirm : Boolean) -> Unit)? = null){

    var builder : AlertDialog.Builder = AlertDialog.Builder(this.activity!!)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setCancelable(isCancelable)
    if ( isChoice ){
        builder.setNegativeButton(android.R.string.cancel){
                dialog, _ ->
            dialog.dismiss()
            completion?.invoke(false)
        }
    }
    builder.setPositiveButton(android.R.string.ok){
            dialog, _ ->
        dialog.dismiss()
        completion?.invoke(true)
    }

    AndroidUtilities.runOnUIThread {
        builder.show()
    }
}

fun Activity.alert(title : String = "", message : String = "", isChoice : Boolean = false, isCancelable : Boolean = false, completion : ((isConfirm : Boolean) -> Unit)? = null){

    var builder : AlertDialog.Builder = AlertDialog.Builder(this)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setCancelable(isCancelable)
    if ( isChoice ){
        builder.setNegativeButton(android.R.string.cancel){
                dialog, _ ->
            dialog.dismiss()
            completion?.invoke(false)
        }
    }
    builder.setPositiveButton(android.R.string.ok){
            dialog, _ ->
        dialog.dismiss()
        completion?.invoke(true)
    }

    AndroidUtilities.runOnUIThread {
        builder.show()
    }
}

fun View.alert(title : String = "", message : String = "", isChoice : Boolean = false, isCancelable : Boolean = false, completion : ((isConfirm : Boolean) -> Unit)? = null){

    var builder : AlertDialog.Builder = AlertDialog.Builder(context)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setCancelable(isCancelable)
    if ( isChoice ){
        builder.setNegativeButton(android.R.string.cancel){
                dialog, _ ->
            dialog.dismiss()
            completion?.invoke(false)
        }
    }
    builder.setPositiveButton(android.R.string.ok){
            dialog, _ ->
        dialog.dismiss()
        completion?.invoke(true)
    }

    AndroidUtilities.runOnUIThread {
        builder.show()
    }
}

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun Activity.changeStatusBarColor(color: Int){
    val window = window
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = color
}

fun TextView.clearDrawable(){
    this.setCompoundDrawables(null, null, null, null)
}

fun TextView.setDrawableLeft(resId : Int){
    val drawable = this.resources.getDrawable(resId, resources.newTheme())
    this.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null)
}

fun TextView.setDrawableRight(resId : Int){
    val drawable = this.resources.getDrawable(resId, resources.newTheme())
    this.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null)
}

