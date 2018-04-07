package com.theevilroot.bashim.app

import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView

class QuotesAdapter(val activity: QuotesActivity): ArrayAdapter<Quote>(activity, R.layout.quotes_item_layout, activity.app.favorites) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = if (convertView == null) {
            LayoutInflater.from(context).inflate(R.layout.quotes_item_layout, null, false)
        } else {
            convertView
        }

        val item = getItem(position)

        with(view) {

            val id = findViewById<TextView>(R.id.quote_id)
            val text =  findViewById<TextView>(R.id.quote_text)

            id.text = "#${item.id}"
            text.text = Html.fromHtml(item.content)

        }

        return view
    }

}