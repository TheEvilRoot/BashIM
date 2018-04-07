package com.theevilroot.bashim.app

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.theevilroot.bashim.app.fragments.FragmentAbyssTop
import com.theevilroot.bashim.app.fragments.FragmentFavorites
import com.theevilroot.bashim.app.fragments.FragmentQuotes
import com.theevilroot.bashim.app.fragments.FragmentRandomQuotes

class BashTabAdapter(fm: FragmentManager,val activity: QuotesActivity) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? = when (position) {
        0 -> FragmentQuotes.newInstance(activity)
        1 -> FragmentRandomQuotes.newInstance(activity)
        2 -> FragmentFavorites.newInstance(activity)
        3 -> FragmentAbyssTop.newInstance(activity)
        else -> null
    }

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> activity.getString(R.string.quotes_title)
        1 -> activity.getString(R.string.random_quotes_title)
        2 -> activity.getString(R.string.favorites_quotes_title)
        3 -> activity.getString(R.string.abyss_top_title)
        else -> ""
    }

    override fun getCount(): Int = 4
}