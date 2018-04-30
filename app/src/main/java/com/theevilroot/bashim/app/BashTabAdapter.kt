package com.theevilroot.bashim.app

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class BashTabAdapter(fm: FragmentManager,val activity: QuotesActivity) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        val holder = activity.navigationFragments.filter { it.id == position }.getOrNull(0)
                ?: return null
        return holder.fragment
    }

    override fun getPageTitle(position: Int): CharSequence {
        val holder = activity.navigationFragments.filter { it.id == position }.getOrNull(0)
                ?: return "[INVALID]"
        return activity.getString(holder.title)
    }

    override fun getCount(): Int = activity.navigationFragments.size
}