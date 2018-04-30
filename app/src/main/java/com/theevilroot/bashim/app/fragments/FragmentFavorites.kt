package com.theevilroot.bashim.app.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.theevilroot.bashim.app.*

class FragmentFavorites : Fragment() {

    lateinit var activity: QuotesActivity
    lateinit var favoriteList: ListView
    lateinit var favoriteTip: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.favorite_fragment, container, false)
        with(view) {
            favoriteList = findViewById(R.id.favorite_quotes_list)
            favoriteTip = findViewById(R.id.favorite_tip)
        }
        updateUI()
        return view
    }
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        try {
            if (isVisibleToUser)
                updateUI()
        }catch (e: Exception){}
    }

    fun updateUI() {
        Log.i("qweqweqeqwe", activity.app.favorites.size.toString())
        activity.runOnUiThread {
            favoriteList.adapter = null
            if(activity.app.favorites.isEmpty()) {
                favoriteTip.text = getString(R.string.no_favotites_text)
                favoriteTip.visibility = View.VISIBLE
                return@runOnUiThread
            }else{
                favoriteTip.visibility = View.GONE
            }
            favoriteList.adapter = FavoriteQuotesAdapter(activity, this, activity.app.favorites.reversed().toTypedArray())
        }
    }

    companion object {
        fun newInstance(activity: QuotesActivity): FragmentFavorites {
            val frg = FragmentFavorites()
            frg.activity = activity
            return frg
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.toolbar_search -> {
                Toast.makeText(activity, "Search", Toast.LENGTH_LONG).show()
                return true
            }
        }
        return false
    }
}