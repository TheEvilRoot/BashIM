package com.theevilroot.bashim.app.fragments

import android.annotation.SuppressLint
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
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import android.widget.EditText
import android.widget.QuickContactBadge
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

            if(currentQuote == null)
                loadToCurrent()
        }
        return view
    }

    private fun loadToCurrent() {
        nextButton.isEnabled = false
        nextButton.showProgress(true)
        thread(start = true, block = {
            try {
                if(app.last < 0)
                    app.last = app.updateLast()
                var rand: Int
                var opt: Quote?
                do {
                    rand = Random().nextInt(app.last) + 1
                    opt = app.loadQuoteById(rand)
                } while (opt == null)
                currentQuote = opt
                updateUI()
                activity.runOnUiThread {
                    nextButton.showProgress(false)
                    nextButton.isEnabled = true
                }
            }catch (e: Exception) {
                activity.runOnUiThread {
                    nextButton.showProgress(false)
                    nextButton.isEnabled = true
                }
                updateUI()
                app.showError(activity, e, getString(R.string.quote_error_text))
            }
        })
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
        fun newInstance(activity:QuotesActivity): FragmentRandomQuotes {
            val frg = FragmentRandomQuotes()
            frg.activity = activity
            frg.app = activity.app
            return frg
        }
    }
}