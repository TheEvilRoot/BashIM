package com.theevilroot.bashim.utils

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.AnimRes
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import java.text.SimpleDateFormat
import java.util.*
import android.net.NetworkInfo
import android.net.ConnectivityManager



fun parseInt(str: String, default: Int): Int = try{ str.toIntOrNull() ?: default } catch (e: Exception) { default }
fun parseLong(str: String, default: Long): Long = try{ str.toLongOrNull() ?: default } catch (e: Exception) { default }
fun parseFloat(str: String, default: Float): Float = try{ str.toFloatOrNull() ?: default } catch (e: Exception) { default }

@SuppressLint("SimpleDateFormat")
fun parseDate(str: String, pattern: String, default: Date): Date = try{ SimpleDateFormat(pattern).parse(str) }catch (e: Exception) { default }

fun makeDate(hours: Int = 12, minutes: Int = 0, seconds: Int = 0, day: Int = 1, month: Int = 1, year: Int = 2000): Date =
        parseDate("$hours:$minutes:$seconds $day.$month.$year", "HH:mm:ss dd.MM.YYYY", Date())
fun <T : View> AppCompatActivity.bind(@IdRes res: Int): Lazy<T> =
        lazy { findViewById<T>(res) }

fun <T : View> RecyclerView.ViewHolder.bindView(@IdRes res: Int): Lazy<T> =
        lazy { itemView.findViewById<T>(res) }

fun Context.load(@AnimRes res: Int): Lazy<Animation> =
        lazy { AnimationUtils.loadAnimation(this, res) }

fun View.hide() {
    this.visibility = View.GONE
}
fun View.hide(animation: Animation) {
    hide()
    if(this.visibility != View.GONE)
        this.startAnimation(animation)
}
fun View.show() {
    this.visibility = View.VISIBLE
}
fun View.show(animation: Animation) {
    show()
    if(this.visibility != View.VISIBLE)
        this.startAnimation(animation)
}
fun View.isVisible() = this.visibility == View.VISIBLE

fun Context.isInternetAvailable():Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo = cm.activeNetworkInfo
    return netInfo != null && netInfo.isConnectedOrConnecting
}