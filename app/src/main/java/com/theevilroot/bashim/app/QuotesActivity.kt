package com.theevilroot.bashim.app
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.MenuCompat
import android.support.v7.app.AlertDialog
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.theevilroot.bashim.app.fragments.FragmentFavorites
import com.theevilroot.bashim.app.fragments.FragmentQuotes
import com.theevilroot.bashim.app.fragments.FragmentRandomQuotes
import java.io.File
import kotlin.concurrent.thread

class QuotesActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var viewPager: NonSwipeViewPager
    lateinit var app: SimpleBash
    lateinit var bottomNavigationView: BottomNavigationView

    lateinit var navigationFragments: Array<FragmentHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quotes)

        app = application as SimpleBash
        viewPager = findViewById(R.id.view_pager)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        toolbar = findViewById(R.id.toolbar)

        navigationFragments = arrayOf(
                FragmentHolder(0, R.id.navigation_quotes, FragmentQuotes.newInstance(this), R.string.quotes_title, R.menu.toolbar_menu),
                FragmentHolder(1, R.id.navigation_random_quotes, FragmentRandomQuotes.newInstance(this), R.string.random_quotes_title, R.menu.toolbar_menu),
                FragmentHolder(2, R.id.navigation_favorites, FragmentFavorites.newInstance(this), R.string.favorites_quotes_title, R.menu.toolbar_menu_favorite)
        )
        setSupportActionBar(toolbar)
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
        invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val holder = navigationFragments.filter { it.navigation ==  bottomNavigationView.selectedItemId}.getOrNull(0)
                ?: return false
        menuInflater.inflate(holder.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val holder = navigationFragments.filter { it.navigation ==  bottomNavigationView.selectedItemId}.getOrNull(0)
                ?: return false
        return holder.fragment.onOptionsItemSelected(item)
    }
}
