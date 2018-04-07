package com.theevilroot.bashim.app.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.theevilroot.bashim.app.Quote
import com.theevilroot.bashim.app.QuotesActivity
import com.theevilroot.bashim.app.R
import com.theevilroot.bashim.app.SimpleBash

class FragmentAbyssTop : Fragment() {

    lateinit var app: SimpleBash
    lateinit var activity: QuotesActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.quotes_fragment, container, false)
        with(view) {

        }
        return view
    }
    companion object {
        fun newInstance(activity: QuotesActivity): FragmentAbyssTop {
            val frg = FragmentAbyssTop()
            frg.activity = activity
            frg.app = activity.app
            return frg
        }
    }
}