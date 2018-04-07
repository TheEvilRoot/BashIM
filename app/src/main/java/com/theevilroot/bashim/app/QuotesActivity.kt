package com.theevilroot.bashim.app

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.File

class QuotesActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    lateinit var app: SimpleBash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quotes)

        app = application as SimpleBash

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        tabLayout = findViewById(R.id.tab_layout)

        viewPager = findViewById(R.id.view_pager)

        val adapter = BashTabAdapter(supportFragmentManager, this)

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.getTabAt(app.lastTab)!!.select()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.toolbar_favorites) {
            AlertDialog.Builder(this).setAdapter(QuotesAdapter(this), {di, i ->
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://bash.im/quote/${app.favorites[i].id}")
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            }).setTitle("Favorites quotes").create().show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        val file = File(filesDir, "simplebash.json")
        val json = JsonObject()
        json.addProperty("lastTab", tabLayout.selectedTabPosition)
        json.addProperty("lastQuote", 1)
        val arr = JsonArray()
        app.favorites.map {
            val obj = JsonObject()
            obj.addProperty("id", it.id)
            obj.addProperty("rate", it.rate)
            obj.addProperty("content", it.content)
            obj.addProperty("url", it.url)
            obj
        }.forEach(arr::add)
        json.add("favorites", arr)
        val text = GsonBuilder().setPrettyPrinting().create().toJson(json)
        file.writeText(text)
    }
}
