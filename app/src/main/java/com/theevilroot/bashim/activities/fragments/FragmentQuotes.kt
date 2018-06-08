package com.theevilroot.bashim.activities.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.google.gson.JsonParser
import com.theevilroot.bashim.R
import com.theevilroot.bashim.activities.ActivityQuotes
import com.theevilroot.bashim.activities.adapters.QuoteListAdapter
import com.theevilroot.bashim.objects.IQuote
import com.theevilroot.bashim.objects.ShowMoreQuote
import com.theevilroot.bashim.utils.CallbackListener
import java.util.*

class FragmentQuotes: TheFragment(), CallbackListener {

    override fun updateUI() {
        try{
            quoteAdapter.notifyDataSetChanged()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private lateinit var activity: ActivityQuotes

    private lateinit var progress: ProgressBar
    private lateinit var quoteList: RecyclerView
    private lateinit var quoteAdapter: QuoteListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = layoutInflater.inflate(R.layout.layout_quotes_fragment, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        progress = view.findViewById(R.id.quotes_progress)
        quoteList = view.findViewById(R.id.quotes_list)
        quoteList.layoutManager = LinearLayoutManager(context)
        quoteAdapter = QuoteListAdapter(activity.app,{
            if(activity.app.quotes.size > 0 && activity.app.quotes.last().isShowMore()) {
                activity.app.quotes.removeAt(activity.app.quotes.lastIndex)
                quoteAdapter.notifyDataSetChanged()
            }
            loadQuotes()
        },{pos ->
            if(pos == null)
                quoteAdapter.notifyDataSetChanged()
            else
                quoteAdapter.notifyItemChanged(pos)
        })
        quoteList.adapter = quoteAdapter
        quoteAdapter.setQuotes(activity.app.quotes)
        loadQuotes()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.test) {

        }
        return true
    }

    private fun putQuote(quote: IQuote) {
        activity.app.quotes.add(quote)
        quoteAdapter.notifyDataSetChanged()
    }

    private fun putShowMore() {
        putQuote(ShowMoreQuote())
    }

    private fun nextStep() {
        if(!activity.app.startLoader(activity.app.session.stepCount, activity.app.session.currentID, this)) {
            Toast.makeText(context, "Unable to load quotes", Toast.LENGTH_SHORT).show()
            if(!activity.app.quotes.last().isShowMore())
                putShowMore()
        }
    }

    private fun loadQuotes() {
        if(activity.app.session.lastID < 0) {
            activity.app.session.requestLastIDAsync(activity, {
                activity.runOnUiThread {
                    if (!it) {
                        Toast.makeText(context, "Unable to load last quote ID", Toast.LENGTH_SHORT).show()
                        if (activity.app.quotes.isEmpty() || !activity.app.quotes.last().isShowMore())
                            putShowMore()
                    } else {
                        nextStep()
                    }
                }
            })
        }else{
            nextStep()
        }
    }

    companion object {
        fun create(activity: ActivityQuotes): FragmentQuotes {
            val fragment = FragmentQuotes()
            fragment.activity = activity
            return fragment
        }
    }

    @Synchronized
    override fun callback(type: CallbackListener.CallbackType, data: Bundle, quote: IQuote?) {
        Log.i("CALLBACK", type.name)
        when (type) {
            CallbackListener.CallbackType.LOADING_STARTED -> {
                activity.runOnUiThread {
                    progress.max = data["count"] as Int
                    progress.progress = 0
                }
            }
            CallbackListener.CallbackType.LOADING_STOPPED -> {
                activity.runOnUiThread {
                    progress.progress = progress.max
                    activity.app.session.currentID = data["last"] as Int
                    putShowMore()
                }
            }
            CallbackListener.CallbackType.NEW_QUOTE -> {
                activity.runOnUiThread {
                    putQuote(quote!!)
                    progress.progress = progress.max - data["left"] as Int
                }
            }
            CallbackListener.CallbackType.LOADING_ERROR -> {
                Log.e("LOADING_ERROR", data.getString("exception"))
            }
        }
    }

}