package com.theevilroot.bashim.objects

import com.google.gson.JsonObject
import com.theevilroot.bashim.TheApplication
import com.theevilroot.bashim.utils.makeDate
import com.theevilroot.bashim.utils.parseDate
import com.theevilroot.bashim.utils.parseInt
import com.theevilroot.bashim.utils.parseLong
import org.jsoup.nodes.Document
import java.util.*

interface IQuote {

    fun id(): Int

    fun date(): Date

    fun rating(): Int

    fun url(): String

    fun text(): String

    fun isShowMore(): Boolean = false

    fun favorite(app: TheApplication): Boolean = app.favoriteList.any { it.id() == id() }

    companion object {
        fun create(doc: Document): IQuote = object: IQuote {
            override fun id(): Int = parseInt(doc.select(".id").text().substring(1), -1)
            override fun date(): Date = parseDate(doc.select(".date").text(),"YYYY-MM-dd HH:mm", makeDate())
            override fun rating(): Int = parseInt(doc.select(".rating").text(), -1)
            override fun url(): String = doc.location()
            override fun text(): String = doc.select(".text").html()
        }
        fun create(obj: JsonObject): IQuote = object: IQuote {
            override fun id(): Int = parseInt(obj["id"].asString, -1)
            override fun date(): Date = Date(parseLong(obj["date"].asString, 0))
            override fun rating(): Int = parseInt(obj["rating"].asString, -1)
            override fun url(): String = obj["url"].asString
            override fun text(): String = obj["text"].asString
        }
    }

}