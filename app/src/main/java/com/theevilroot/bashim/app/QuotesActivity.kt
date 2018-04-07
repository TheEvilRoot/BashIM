package com.theevilroot.bashim.app

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
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
    lateinit var viewPager: ViewPager
    lateinit var app: SimpleBash
    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quotes)

        app = application as SimpleBash

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        viewPager = findViewById(R.id.view_pager)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        val adapter = BashTabAdapter(supportFragmentManager, this)

        viewPager.adapter = adapter
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.navigation_quotes -> {
                    viewPager.setCurrentItem(0, true)
                    title = viewPager.adapter!!.getPageTitle(0)
                }
                R.id.navigation_random_quotes -> {
                    viewPager.setCurrentItem(1, true)
                    title = viewPager.adapter!!.getPageTitle(1)
                }
                R.id.navigation_favorites -> {
                    viewPager.setCurrentItem(2, true)
                    title = viewPager.adapter!!.getPageTitle(2)
                }
                R.id.navigation_abyss_top -> {
                    viewPager.setCurrentItem(3, true)
                    title = viewPager.adapter!!.getPageTitle(3)
                }
            }
            true
        }
    }

    override fun onStop() {
        super.onStop()
        val file = File(filesDir, "simplebash.json")
        val json = JsonObject()
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
