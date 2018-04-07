package com.theevilroot.bashim.app

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.theevilroot.bashim.app.fragments.FragmentQuotes
import com.theevilroot.bashim.app.fragments.FragmentRandomQuotes

class BashTabAdapter(fm: FragmentManager,val activity: QuotesActivity) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? = when (position) {
        0 -> FragmentQuotes.newInstance(activity.app,activity)
        1 -> FragmentRandomQuotes.newInstance(activity.app, activity)
        else -> null
    }

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> "Цитаты"
        1 -> "Рандомные цитаты"
        else -> ""
    }

    override fun getCount(): Int = 2
}