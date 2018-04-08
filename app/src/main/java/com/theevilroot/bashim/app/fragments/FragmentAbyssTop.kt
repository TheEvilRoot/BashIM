package com.theevilroot.bashim.app.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import com.theevilroot.bashim.app.*
import org.jsoup.Jsoup
import kotlin.concurrent.thread

class FragmentAbyssTop : Fragment() {

    lateinit var app: SimpleBash
    lateinit var activity: QuotesActivity
    lateinit var topList: ListView
    lateinit var statusView: TextView

    var abyssTop: Array<TopQuote> = emptyArray()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.abysstop_fragment, container, false)
        with(view) {
            topList = findViewById(R.id.abyss_top_list)
            statusView = findViewById(R.id.abyss_top_status_view)
        }

        if(abyssTop.isEmpty())
            load()

        return view
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        try {
            if (isVisibleToUser) {
            }
        }catch (e: Exception){}
    }

    private fun loadAbyssTop(): Triple<Boolean,String ,Array<TopQuote>> {
        val response = Jsoup.connect("https://bash.im/abysstop").followRedirects(false).execute()
        if(response.statusCode() != 200)
            return Triple(false, response.statusMessage(), emptyArray())
        val doc = response.parse()
        return Triple(true, "OK", doc.select("#body").select(".quote").map { TopQuote(it.select(".abysstop").text(), it.select(".abysstop-date").text(), it.select(".text").html()) }.toTypedArray())
    }

    fun updateUI() {
        activity.runOnUiThread {
            if(abyssTop.isNotEmpty())
                statusView.visibility = View.GONE
            topList.adapter = AbyssTopQuotesAdapter(activity, this, abyssTop)
        }
    }

    fun load() {
        statusView.text = "Загрузка топа..."
        statusView.visibility = View.VISIBLE
        thread(start = true, block = {
            val ret = loadAbyssTop()
            if(!ret.first) {
                activity.runOnUiThread { statusView.text = "Ошибка загрузки: ${ret.second}" }
                return@thread
            }
            abyssTop = ret.third
            updateUI()
        })
    }

    companion object {
        fun newInstance(activity: QuotesActivity): FragmentAbyssTop {
            val frg = FragmentAbyssTop()
            frg.activity = activity
            frg.app = activity.app
            return frg
        }
    }
}