package com.theevilroot.bashim.app.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.theevilroot.bashim.app.QuotesActivity
import com.theevilroot.bashim.app.R
import com.theevilroot.bashim.app.SimpleBash

class FragmentQuotes : Fragment() {

    lateinit var app: SimpleBash
    lateinit var activity: QuotesActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.quotes_fragment, container, false)
        with(view) {

        }
        return view
    }
    companion object {
        fun newInstance(app:SimpleBash, activity: QuotesActivity): FragmentQuotes  {
            val frg = FragmentQuotes()
            frg.app = app
            frg.activity = activity
            return frg
        }
    }
}