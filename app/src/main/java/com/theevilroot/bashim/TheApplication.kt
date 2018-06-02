package com.theevilroot.bashim

import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.util.Log
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.theevilroot.bashim.activities.ActivityQuotes
import com.theevilroot.bashim.activities.fragments.FragmentQuotes
import com.theevilroot.bashim.activities.fragments.IFragment
import com.theevilroot.bashim.objects.IQuote
import com.theevilroot.bashim.objects.Session
import com.theevilroot.bashim.utils.CallbackListener
import com.theevilroot.bashim.utils.isInternetAvailable
import org.jsoup.Jsoup
import java.io.File

class TheApplication: Application() {

    lateinit var quotes: ArrayList<IQuote>
    lateinit var fragments: List<IFragment>
    lateinit var session: Session
    lateinit var favoriteList: ArrayList<IQuote>

    var isFavoriteLoaded = false

    var loadingThread: Thread? = null
    val loadingRunnable = object: Runnable {
        override fun run() {
            var i = from;
            try {
                callbackListener?.callback(CallbackListener.CallbackType.LOADING_STARTED, Bundle().apply { this.putInt("count", count) })
                while (count > 0) {
                    val doc = Jsoup.connect("https://bash.im/quote/${i--}").followRedirects(false).execute()
                    if(doc.statusCode() != 200)
                        continue
                    count--
                    callbackListener?.callback(CallbackListener.CallbackType.NEW_QUOTE, Bundle().apply {
                        this.putInt("left", count)
                    }, IQuote.Companion.create(doc.parse()))
                }
                isLoaderWorking = false
                callbackListener?.callback(CallbackListener.CallbackType.LOADING_STOPPED, Bundle().apply { this.putInt("last", i) })
            }catch (e:Exception) {
                e.printStackTrace()
                callbackListener?.callback(CallbackListener.CallbackType.LOADING_ERROR, Bundle().apply { this.putString("exception", e.javaClass.name); this.putString("msg",e.message) })
            }
        }
    }
    var count = 0
    var from = 0
    var callbackListener: CallbackListener? = null
    var isLoaderWorking = false

    override fun onCreate() {
        super.onCreate()
        quotes = ArrayList()
        session = Session()
        favoriteList = ArrayList()
    }

    fun startLoader(count: Int,from: Int ,listener: CallbackListener): Boolean{
        if(isLoaderWorking || count <= 0 || !applicationContext.isInternetAvailable())
            return false
        isLoaderWorking = true
        this.count = count
        this.from = from
        this.callbackListener = listener
        loadingThread = Thread(loadingRunnable)
        loadingThread!!.start()
        Log.i("LOADER", "Started loading from $from to $count quotes!")
        return true
    }

    fun addToFavorite(quote: IQuote) {
        favoriteList.add(quote)
    }

    fun removeFromFavorite(quote: IQuote) {
        favoriteList.removeIf { it.id() == quote.id() }
    }

    fun loadFavorites() {
        val file = File(filesDir, "fav.json")
        if(!file.exists())
            return
        try {
            val json = JsonParser().parse(file.readText()).asJsonArray
            json.map { it.asJsonObject }.map { IQuote.create(it) }.forEach { favoriteList.add(it) }
        }catch (e: Exception) {
            Toast.makeText(this, "An error occurred while loading favorite file. Deleting this!", Toast.LENGTH_SHORT).show()
            file.deleteOnExit()
            e.printStackTrace()
        }
    }

    fun saveFavirites() {
        val file = File(filesDir, "fav.json")
        try {
            if (!file.exists())
                file.createNewFile()
            val json = JsonArray()
            favoriteList.map {
                val obj = JsonObject()
                obj.addProperty("id", it.id())
                obj.addProperty("date", it.date().time)
                obj.addProperty("rating", it.rating())
                obj.addProperty("url", it.url())
                obj.addProperty("text", it.text())
                obj
            }.forEach { json.add(it) }
            file.writeText(GsonBuilder().setPrettyPrinting().create().toJson(json))
        }catch (e: Exception) {
            Toast.makeText(this, "An error occurred while saving favorite file.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}