package com.theevilroot.bashim.objects

import android.content.Context
import com.theevilroot.bashim.utils.isInternetAvailable
import com.theevilroot.bashim.utils.parseInt
import org.jsoup.Jsoup
import kotlin.concurrent.thread

class Session {

    var stepCount = 10 // Will specified in Preferences
    var currentID = 0 // Will load from Account or Cache
    var lastID = -1 // Will load on request {@link #requestLastID()}

    fun requestLastIDAsync(context: Context,onLoad: (Boolean) -> Unit) {
        thread(true) { onLoad(requestLastIDBlock(context)) }
    }
    /**
     *  MUST run NOT in UI Thread
     */
    fun requestLastIDBlock(context: Context): Boolean {
        if(!context.isInternetAvailable())
            return false
        val result = requestLastID()
        if(result < 0)
            return false
        lastID = result
        currentID = lastID
        return true
    }

    private fun requestLastID(): Int{
        val doc = Jsoup.connect("https://bash.im/").get()
        return parseInt(doc.select(".quote")[0].select(".id").text().replace("\\D".toRegex(), ""), -1)
    }

}