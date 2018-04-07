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
import com.theevilroot.bashim.app.fragments.FragmentFavorites

class FavoriteQuotesAdapter(val activity: QuotesActivity,val fragment: FragmentFavorites, items: Array<Quote>): ArrayAdapter<Quote>(activity, R.layout.favorite_quote_item_layout, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.favorite_quote_item_layout, null, false)

        val item = getItem(position)

        with(view) {

            val id = findViewById<TextView>(R.id.fav_quote_item_id)
            val url =  findViewById<TextView>(R.id.fav_quote_item_url)
            val text =  findViewById<TextView>(R.id.fav_quote_item_text)
            val rate =  findViewById<TextView>(R.id.fav_quote_item_rate)
            val removeButton =  findViewById<Button>(R.id.fav_quote_item_remove)
            val shareButton =  findViewById<Button>(R.id.fav_quote_item_share)

            id.text = "#${item.id}"
            url.text = item.url
            rate.text = "[ ${item.rate} ]"
            text.text = Html.fromHtml(item.content)

            removeButton.setOnClickListener {
                if(activity.app.favorites.any { it.id == item.id }) {
                    activity.app.favorites.remove(item)
                    fragment.updateUI(false)
                }
            }

            shareButton.setOnClickListener {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://bash.im/quote/${item.id}")
                sendIntent.type = "text/plain"
                activity.startActivity(sendIntent)
            }

        }

        return view
    }

}