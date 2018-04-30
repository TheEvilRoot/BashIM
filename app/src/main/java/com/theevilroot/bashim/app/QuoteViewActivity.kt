package com.theevilroot.bashim.app

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import java.util.regex.Pattern
import kotlin.concurrent.thread

class QuoteViewActivity: AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var contentView: TextView
    lateinit var quote: Quote
    lateinit var app: SimpleBash

    var quoteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quote_view)

        app = application as SimpleBash
        toolbar = findViewById(R.id.toolbar)
        contentView = findViewById(R.id.quote_content)

        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        with(getSharedData()) {
            if(this == null) {
                supportActionBar!!.title = getString(R.string.invalid_quote_id)
                return
            }
            quoteId = this
        }
        thread(true) {
            with(app.loadQuoteById(quoteId)) {
                if(this == null) {
                    runOnUiThread { toolbar.title = getString(R.string.invalid_quote_id) }
                    return@thread
                }
                quote = this
            }
            updateUI()
        }
    }

    private fun getSharedData():Int? {
        val intentId = intent.getIntExtra("QUOTE_ID", -1)
        val intentData = intent.data.toString()
        if(intentId > 0)
            return intentId
        val re = "https?://bash\\.im/quote/(\\d+)".toRegex()
        return re.find(intentData)?.groups?.get(1)?.value?.toInt() ?: return null
    }

    private fun updateUI() {
        runOnUiThread {
            toolbar.title = "#${quote.id}"
            toolbar.subtitle = quote.url
            contentView.text = Html.fromHtml(quote.content)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.quote_view_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.quote_view_like -> {
                if (app.isFavorite(quote)) {
                    Toast.makeText(this, getString(R.string.already_favorite), Toast.LENGTH_SHORT).show()
                    return false
                }
                app.favorites.add(quote)
                return true
            }
            R.id.quote_view_share -> {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://bash.im/quote/${quote.id}")
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            }
            android.R.id.home -> {
                this.finish()
            }
        }
        return true
    }

}