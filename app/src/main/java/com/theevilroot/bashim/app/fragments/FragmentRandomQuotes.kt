package com.theevilroot.bashim.app.fragments

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import mbanje.kurt.fabbutton.FabButton
import org.jsoup.Jsoup
import java.util.*
import kotlin.concurrent.thread
import android.content.Intent
import com.theevilroot.bashim.app.Quote
import com.theevilroot.bashim.app.QuotesActivity
import com.theevilroot.bashim.app.R
import com.theevilroot.bashim.app.SimpleBash


class FragmentRandomQuotes : Fragment() {

    lateinit var idView: TextView
    lateinit var rateView: TextView
    lateinit var contentView: TextView
    lateinit var nextButton: FabButton
    lateinit var likeButton: FabButton
    lateinit var shareButton: FabButton
    lateinit var app: SimpleBash
    lateinit var activity: QuotesActivity

    var currentQuote: Quote? = null

    var lock: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.random_quotes_fragment, container, false)
        with(view) {
            idView = findViewById(R.id.random_quote_id)
            rateView = findViewById(R.id.random_quote_rate)
            contentView = findViewById(R.id.random_quote_content)
            nextButton = findViewById(R.id.random_next_button)
            likeButton = findViewById(R.id.random_like_button)
            shareButton = findViewById(R.id.random_share_button)

            nextButton.setIndeterminate(true)
            shareButton.setIndeterminate(true)
            nextButton.showProgress(false)
            likeButton.showProgress(false)
            shareButton.showProgress(false)

            nextButton.setOnClickListener {
                loadToCurrent()
            }

            likeButton.setOnClickListener {
                if(currentQuote != null) {
                    app.favorites.add(currentQuote!!)
                    updateUI()
                }
            }

            shareButton.setOnClickListener {
                if(currentQuote != null) {
                    shareButton.showProgress(true)
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "https://bash.im/quote/${currentQuote!!.id}")
                    sendIntent.type = "text/plain"
                    startActivity(sendIntent)
                    shareButton.showProgress(false)
                }
            }

            if(currentQuote == null)
                loadToCurrent()
        }
        return view
    }

    private fun loadToCurrent() {
        nextButton.showProgress(true)
        thread(start = true, block = {
            val last = app.getLast()
            var rand = Random().nextInt(last)+1
            while(!app.isQuoteExists(rand)) {
                rand = Random().nextInt(last)+1
            }
            currentQuote = app.loadQuoteById(rand)
            updateUI()
            activity.runOnUiThread {
                nextButton.showProgress(false)
            }
        })
    }

    private fun updateUI() {
        if(currentQuote == null)
            return
        activity.runOnUiThread {
            idView.text ="#${currentQuote!!.id}"
            rateView.text = "[ ${currentQuote!!.rate} ]"
            contentView.text = Html.fromHtml(currentQuote!!.content)
            likeButton.setIcon(activity.getDrawable(
                    if(app.isFavorite(currentQuote!!)) {
                        R.drawable.ic_heart_white_18dp
                    }else{
                        R.drawable.ic_heart_outline_white_18dp
                    }),
                    activity.getDrawable(R.drawable.ic_fab_complete)
            )
        }
    }

    companion object {
        fun newInstance(app: SimpleBash, activity:QuotesActivity): FragmentRandomQuotes {
            val frg = FragmentRandomQuotes()
            frg.app = app
            frg.activity = activity
            return frg
        }
    }
}