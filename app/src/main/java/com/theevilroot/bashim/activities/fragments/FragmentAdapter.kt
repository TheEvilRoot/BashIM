package com.theevilroot.bashim.activities.fragments

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.theevilroot.bashim.R
import com.theevilroot.bashim.TheApplication

class FragmentAdapter(val app: TheApplication, manager: FragmentManager): FragmentPagerAdapter(manager) {

    fun getTitle(position: Int): String {
        val holder = app.fragments.filter { it.id() == position }.getOrNull(0) ?: return "INVALID"
        return app.getString(holder.title())
    }

    override fun getItem(position: Int): Fragment? {
        val holder = app.fragments.filter { it.id() == position }.getOrNull(0) ?: return null
        return holder.fragment()
    }

    override fun getCount(): Int = app.fragments.count()

}