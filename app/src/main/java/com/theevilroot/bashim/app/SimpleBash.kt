package com.theevilroot.bashim.app

import android.app.Application
import android.util.Log
import org.jsoup.Jsoup

class SimpleBash: Application() {

    var lastTab: Int = 0
    var lastQuote: Int = 0
    var favorites: ArrayList<Quote> = ArrayList()

    override fun onCreate() {
        super.onCreate()
    }

    fun getLast() =
        Jsoup.connect("https://bash.im").get().select(".quote").get(0).select(".id").text().replace("\\D".toRegex(), "").toInt()

    fun isQuoteExists(id: Int):Boolean {
        val doc = Jsoup.connect("https://bash.im/quote/$id").followRedirects(false).execute()
        return doc.statusCode() == 200
    }

    fun loadQuoteById(id: Int):Quote {
        val doc = Jsoup.connect("https://bash.im/quote/$id").get()
        return Quote("$id", doc.select(".rating").text(), doc.select(".text").html(), "https://bash.im/quote/$id")
    }

    fun isFavorite(quote:Quote):Boolean = favorites.any { it.id == quote.id }
}