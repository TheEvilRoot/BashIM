package com.theevilroot.bashim.app.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.text.Html
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.theevilroot.bashim.app.*
import mbanje.kurt.fabbutton.FabButton
import org.jsoup.Jsoup
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeoutException
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class FragmentQuotes : Fragment() {

    lateinit var app: SimpleBash
    lateinit var activity: QuotesActivity

    lateinit var idView:TextView
    lateinit var rateView:TextView
    lateinit var contentView:TextView
    lateinit var nextButton: FabButton
    lateinit var prevButton: FabButton
    lateinit var shareButton: FabButton
    lateinit var likeButton: FabButton

    var currentQuoteId: Int = 1
    private var currentQuote: Quote? = null
    private var quoteHistory: ArrayList<Int> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.quotes_fragment, container, false)
        with(view) {
            idView = findViewById(R.id.quote_id)
            rateView = findViewById(R.id.quote_rate)
            contentView = findViewById(R.id.quote_content)
            nextButton = findViewById(R.id.quote_next_button)
            prevButton = findViewById(R.id.quote_prev_button)
            shareButton = findViewById(R.id.quote_share_button)
            likeButton = findViewById(R.id.quote_like_button)
        }

        nextButton.setOnClickListener {
            loadNext()
        }
        prevButton.setOnClickListener {
            loadPrev()
        }

        likeButton.setOnClickListener {
            if(currentQuote != null) {
                if(app.favorites.any { it.id == currentQuote!!.id }) {
                    app.favorites.remove(currentQuote!!)
                }else {
                    app.favorites.add(currentQuote!!)
                }
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

        currentQuoteId = app.lastQuote

        loadCurrent()

        return view
    }

    @SuppressLint("SetTextI18n")
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

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        try {
            if (isVisibleToUser)
                updateUI()
        }catch (e: Exception){}
    }

    private fun loadPrev() {
        disableControlUI()
        prevButton.showProgress(true)
        thread(true) {
            if(app.last < 0)
                app.last = app.updateLast()
            if (quoteHistory.isEmpty() || quoteHistory.first() == currentQuoteId)
                with(loadBackward()) {
                    currentQuoteId = first
                    currentQuote = second
                }
            else
                with(quoteHistory[quoteHistory.indexOf(currentQuoteId) - 1]) {
                    currentQuoteId = this
                    currentQuote = app.loadQuoteById(currentQuoteId)
                }
            updateUI()
            enableControlUI()
            activity.runOnUiThread { prevButton.showProgress(false) }
        }
    }

    private fun loadNext() {
        nextButton.showProgress(true)
        disableControlUI()
        thread(true) {
            if(app.last < 0)
                app.last = app.updateLast()
            if (quoteHistory.isEmpty() || quoteHistory.last() == currentQuoteId)
                with(loadForward()) {
                    currentQuoteId = first
                    currentQuote = second
                }
            else
                with(quoteHistory[quoteHistory.indexOf(currentQuoteId) + 1]) {
                    currentQuoteId = this
                    currentQuote = app.loadQuoteById(currentQuoteId)
                }
            updateUI()
            enableControlUI()
            activity.runOnUiThread { nextButton.showProgress(false) }
        }
    }

    private fun loadForward(): Pair<Int, Quote> {
        var quote: Quote?
        var id: Int = currentQuoteId
        var attempts: Int = 0
        do {
            if(++attempts > 10000) {
                throw TimeoutException("Too many attempts")
            }
            if(++id > app.last) {
                activity.runOnUiThread { activity.toolbar.setSubtitle(R.string.last_quote) }
                return quoteHistory.lastIndex to app.loadQuoteById(quoteHistory.last())!!
            }
            quote = app.loadQuoteById(id)
        }while (quote == null)
        return id to quote
    }

    private fun loadBackward(): Pair<Int, Quote> {
        var quote: Quote?
        var id: Int = currentQuoteId
        var attempts: Int = 0
        do {
            if(++attempts > 10000) {
                throw TimeoutException("Too many attempts")
            }
            if(--id < 1) {
                activity.runOnUiThread { activity.toolbar.setSubtitle(R.string.first_quote) }
                return quoteHistory.lastIndex to app.loadQuoteById(quoteHistory.last())!!
            }
            quote = app.loadQuoteById(id)
        }while (quote == null)
        return id to quote
    }


    fun loadCurrent() {
        nextButton.showProgress(true)
        prevButton.showProgress(true)
        disableControlUI()
        thread(true) {
            if(app.last < 0)
                app.last = app.updateLast()
            currentQuote = app.loadQuoteById(currentQuoteId)
            updateUI()
            enableControlUI()
            activity.runOnUiThread { nextButton.showProgress(false); prevButton.showProgress(false) }
        }
    }

    private fun disableControlUI() {
        activity.runOnUiThread {
            nextButton.isEnabled = false
            prevButton.isEnabled = false
        }
    }

    private fun enableControlUI() {
        activity.runOnUiThread {
            nextButton.isEnabled = true
            prevButton.isEnabled = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.toolbar_update_last_option -> thread(true) { app.last = app.updateLast() }
            R.id.toolbar_jump_to_quote -> {
                val view = layoutInflater.inflate(R.layout.jtq_layout, null)
                AlertDialog.Builder(activity).setTitle(R.string.jtq_title).setView(view).setPositiveButton(R.string.jump_title, { di, _ ->
                    val field = view.findViewById<EditText>(R.id.jtq_field)
                    if(field.text.isNotBlank()) {
                        val id = field.text.toString().toIntOrNull() ?: return@setPositiveButton
                        with(activity) {
                            if (viewPager.currentItem != navigationFragments[0].id) {
                                select(navigationFragments[0])
                                bottomNavigationView.selectedItemId = navigationFragments[0].navigation
                            }
                            val fragment = navigationFragments[0].fragment as FragmentQuotes
                            fragment.currentQuoteId = id
                            fragment.loadCurrent()
                        }
                    }
                }).create().show()
                return true
            }
        }
        return false
    }

    companion object {
        fun newInstance(activity: QuotesActivity): FragmentQuotes  {
            val frg = FragmentQuotes()
            frg.activity = activity
            frg.app = activity.app
            return frg
        }
    }

    val Boolean.int
            get() = if(this) 1 else 0
}