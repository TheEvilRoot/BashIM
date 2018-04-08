package com.theevilroot.bashim.app

import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.theevilroot.bashim.app.fragments.FragmentAbyssTop
import com.theevilroot.bashim.app.fragments.FragmentFavorites

class AbyssTopQuotesAdapter(val activity: QuotesActivity,val fragment: FragmentAbyssTop, items: Array<TopQuote>): ArrayAdapter<TopQuote>(activity, R.layout.abysstop_quote_layout, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.abysstop_quote_layout, null, false)

        val item = getItem(position)

        with(view) {

            val pos = findViewById<TextView>(R.id.abyss_top_quote_pos)
            val date =  findViewById<TextView>(R.id.abyss_top_quote_date)
            val text =  findViewById<TextView>(R.id.abyss_top_quote_text)

            pos.text = item.topPos
            date.text = item.date
            text.text = Html.fromHtml(item.content)
        }
        return view
    }

}