package com.theevilroot.bashim.app

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.View
import org.apache.commons.lang.exception.ExceptionUtils
import org.jsoup.Jsoup
import java.util.*

class SimpleBash: Application() {

    var last: Int = -1
    var lastTab: Int = 0
    var lastQuote: Int = 0
    var favorites: ArrayList<Quote> = ArrayList()

    override fun onCreate() {
        super.onCreate()
    }

    fun updateLast() =
        Jsoup.connect("https://bash.im").get().select(".quote")[0].select(".id").text().replace("\\D".toRegex(), "").toInt()

    fun loadQuoteById(id: Int):Quote? {
        try {
            val response = Jsoup.connect("https://bash.im/quote/$id").followRedirects(false).execute()
            if (response.statusCode() != 200)
                return null
            val doc = response.parse()
            return Quote("$id", doc.select(".rating").text(), doc.select(".text").html(), "https://bash.im/quote/$id")
        }catch (e: Exception){
            throw e
        }
    }

    fun showError(context: Activity, e: Exception? = null, msg: String = getString(R.string.default_error_text), fatal: Boolean = false) {
        context.runOnUiThread {
            AlertDialog.Builder(context, R.style.AppTheme_Dialog_Error).setTitle(getString(R.string.error_title)).setIcon(R.drawable.alert_circle_outline).setMessage(msg).setPositiveButton(R.string.copy_text, { di, i ->
                val clipboard = context.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("error", if (e == null) "[ERROR]" else ExceptionUtils.getFullStackTrace(e))
                clipboard.primaryClip = clip
            }).setCancelable(!fatal).create().show()
        }
        e?.printStackTrace()
    }

    fun isFavorite(quote:Quote):Boolean = favorites.any { it.id == quote.id }
}