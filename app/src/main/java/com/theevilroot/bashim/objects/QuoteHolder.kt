package com.theevilroot.bashim.objects

import android.annotation.SuppressLint
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.theevilroot.bashim.R
import com.theevilroot.bashim.TheApplication
import com.theevilroot.bashim.utils.bindView
import com.theevilroot.bashim.utils.hide
import com.theevilroot.bashim.utils.show
import java.text.SimpleDateFormat

class QuoteHolder(itemView:View): RecyclerView.ViewHolder(itemView){

    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("YYYY-MM-dd HH:mm")

    private val id: TextView = itemView.findViewById(R.id.quote_item_id)
    private val date: TextView = itemView.findViewById(R.id.quote_item_date)
    private val text: TextView = itemView.findViewById(R.id.quote_item_text)
    private val rating: TextView = itemView.findViewById(R.id.quote_item_rating)
    private val like: ImageButton = itemView.findViewById(R.id.quote_item_like)
    private val dislike: ImageButton = itemView.findViewById(R.id.quote_item_dislike)
    private val titlebar: View = itemView.findViewById(R.id.quote_item_titlebar)
    private val footer: View = itemView.findViewById(R.id.quote_item_footer)
    private val favorite: ImageButton = itemView.findViewById(R.id.quote_item_favorite)
    @SuppressLint("SetTextI18n")
    fun bind(quote: IQuote,app: TheApplication ,onShowMore:() -> Unit, notifyDataSetChanged: (Int?) -> Unit) {
        if(quote.isShowMore()) {
            titlebar.show()
            footer.hide()
            text.hide()
            date.hide()
            id.text = "Загрузить ещё..."
            itemView.setOnClickListener {
                onShowMore()
            }
        }else {
            titlebar.show()
            footer.show()
            text.show()
            date.show()
            id.text = "#${quote.id()}"
            date.text = dateFormat.format(quote.date())
            text.text = Html.fromHtml(quote.text())
            rating.text = quote.rating().toString()
            if(quote.favorite(app)) {
                favorite.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.favorite))
                favorite.setColorFilter(Color.RED)
                favorite.setOnClickListener {
                    app.removeFromFavorite(quote)
                    notifyDataSetChanged.invoke(null)
                }
            }else{
                favorite.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.favorite_outline))
                favorite.setColorFilter(itemView.context.resources.getColor(R.color.colorItem))
                favorite.setOnClickListener {
                    app.addToFavorite(quote)
                    notifyDataSetChanged.invoke(null)
                }
            }
            like.setOnClickListener {
                Toast.makeText(itemView.context, "Like quote #${id.text}", Toast.LENGTH_SHORT).show()
            }
            dislike.setOnClickListener {
                Toast.makeText(itemView.context, "Dislike quote #${id.text}", Toast.LENGTH_SHORT).show()
            }
            itemView.setOnClickListener {
                Toast.makeText(itemView.context, "Quote!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}