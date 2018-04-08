package com.theevilroot.bashim.app
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.theevilroot.bashim.app.fragments.FragmentAbyssTop
import com.theevilroot.bashim.app.fragments.FragmentFavorites
import com.theevilroot.bashim.app.fragments.FragmentQuotes
import com.theevilroot.bashim.app.fragments.FragmentRandomQuotes
import java.io.File

class QuotesActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var viewPager: ViewPager
    lateinit var app: SimpleBash
    lateinit var bottomNavigationView: BottomNavigationView

    lateinit var navigationFragments: Array<FragmentHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quotes)

        app = application as SimpleBash

        navigationFragments = arrayOf(
                FragmentHolder(0, R.id.navigation_quotes, FragmentQuotes.newInstance(this), R.string.quotes_title),
                FragmentHolder(1, R.id.navigation_abyss_top, FragmentAbyssTop.newInstance(this), R.string.abyss_top_title),
                FragmentHolder(2, R.id.navigation_random_quotes, FragmentRandomQuotes.newInstance(this), R.string.random_quotes_title),
                FragmentHolder(3, R.id.navigation_favorites, FragmentFavorites.newInstance(this), R.string.favorites_quotes_title)
        )

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        viewPager = findViewById(R.id.view_pager)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        val adapter = BashTabAdapter(supportFragmentManager, this)

        viewPager.adapter = adapter

        bottomNavigationView.setOnNavigationItemSelectedListener {item ->

            val holder = navigationFragments.filter { it.navigation ==  item.itemId}.getOrNull(0)
                    ?: return@setOnNavigationItemSelectedListener false
            select(holder)
            true
        }
        with(navigationFragments.filter { it.id == app.lastTab }.getOrNull(0)){
            if(this == null)
                return@with
            select(this)
            bottomNavigationView.selectedItemId = this.navigation
        }
    }

    override fun onStop() {
        super.onStop()
        val file = File(filesDir, "simplebash.json")
        val json = JsonObject()
        json.addProperty("lastQuote", 1)
        json.addProperty("lastTab", viewPager.currentItem)
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

    fun select(holder: FragmentHolder) {
        viewPager.setCurrentItem(holder.id, true)
        title = viewPager.adapter!!.getPageTitle(holder.id)
    }
}
