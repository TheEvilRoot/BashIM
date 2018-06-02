package com.theevilroot.bashim.activities

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import com.theevilroot.bashim.R
import com.theevilroot.bashim.TheApplication
import com.theevilroot.bashim.activities.fragments.FragmentAdapter
import com.theevilroot.bashim.activities.fragments.FragmentQuotes
import com.theevilroot.bashim.activities.fragments.FragmentSettings
import com.theevilroot.bashim.activities.fragments.IFragment
import com.theevilroot.bashim.utils.*
import kotlin.concurrent.thread

class ActivityQuotes: AppCompatActivity() {

    val layout = R.layout.layout_quotes_activity

    lateinit var fragmentAdapter: FragmentAdapter

    lateinit var app: TheApplication
    val toolbar: Toolbar by bind(R.id.quotes_toolbar)
    val viewPager: ViewPager by bind(R.id.quotes_view_pager)

    val slideInAnimation:Animation by load(R.anim.abc_slide_in_top)
    val slideOutAnimation:Animation by load(R.anim.abc_slide_out_top)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        app = application as TheApplication
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        app.fragments = listOf(
                IFragment.create(0,FragmentQuotes.create(this), R.string.quotes_title),
                IFragment.create(1,FragmentSettings.create(this), R.string.settings_title))
        fragmentAdapter = FragmentAdapter(app, supportFragmentManager)
        viewPager.adapter = fragmentAdapter
        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {}
        })
        app.loadFavorites()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.test_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        app.fragments[viewPager.currentItem].fragment().onOptionsItemSelected(item)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        app.saveFavirites()
    }

}