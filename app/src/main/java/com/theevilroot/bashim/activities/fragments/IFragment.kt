package com.theevilroot.bashim.activities.fragments
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment

interface IFragment {

    fun id(): Int

    fun fragment(): TheFragment

    @StringRes
    fun title(): Int

    companion object {
        fun create(id: Int, fragment: TheFragment, @StringRes title: Int) = object: IFragment {
            override fun id(): Int = id
            override fun fragment(): TheFragment = fragment
            override fun title(): Int = title
        }
    }
}