package com.theevilroot.bashim.activities.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.theevilroot.bashim.R
import com.theevilroot.bashim.TheApplication
import com.theevilroot.bashim.objects.IQuote
import com.theevilroot.bashim.objects.QuoteHolder

class QuoteListAdapter(private val app: TheApplication,private val onShowMore:() -> Unit, private val notifyDataSetChanged: (Int?) -> Unit): RecyclerView.Adapter<QuoteHolder>() {

    private var quotes: List<IQuote> = emptyList()

    fun setQuotes(e: List<IQuote>) {
        this.quotes = e
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_quote, parent, false)
        return QuoteHolder(view)
    }

    override fun onBindViewHolder(holder: QuoteHolder, position: Int) {
        holder.bind(quotes[position],app,onShowMore,notifyDataSetChanged)
    }
    override fun getItemCount(): Int = quotes.count()
}