package com.theevilroot.bashim.activities.fragments

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.theevilroot.bashim.R
import com.theevilroot.bashim.activities.ActivityQuotes
import com.theevilroot.bashim.activities.adapters.SettingMenuAdapter
import com.theevilroot.bashim.objects.SettingEntry
import kotlin.math.roundToInt

class FragmentSettings: TheFragment() {
    override fun updateUI() {}

    private lateinit var activity: ActivityQuotes
    private lateinit var settingsList: RecyclerView
    private lateinit var settingsAdapter: SettingMenuAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = layoutInflater.inflate(R.layout.layout_settings_fragment, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        settingsList = view.findViewById(R.id.settings_list)
        settingsList.layoutManager = LinearLayoutManager(context)
        settingsAdapter = SettingMenuAdapter()
        settingsList.adapter = settingsAdapter
        settingsAdapter.setEntries(listOf(
                SettingEntry(R.string.favorite_quotes_title, R.drawable.favorite, true, {_,_ ->
                    Toast.makeText(context, "Favorite quotes", Toast.LENGTH_SHORT).show()
                }),
                SettingEntry(R.string.preferences_title, R.drawable.preferences, true, {_,_ ->
                    Toast.makeText(context, "Preferences", Toast.LENGTH_SHORT).show()
                }),
                SettingEntry(R.string.about_title, R.drawable.about, true, {_,_ ->
                    Toast.makeText(context, "About", Toast.LENGTH_SHORT).show()
                }),
                SettingEntry(R.string.logout_title, R.drawable.exit, true, {_,_ ->
                    Toast.makeText(context, "Log out", Toast.LENGTH_SHORT).show()
                },{itemView, iconView, titleView,_ ->
                    val scale = itemView.context.resources.displayMetrics.density
                    (itemView.layoutParams as ViewGroup.MarginLayoutParams).setMargins(0,(scale * 24).roundToInt(),0,0)
                    titleView.setTextColor(Color.RED)
                })
        ))
    }

    companion object {
        fun create(activity: ActivityQuotes): FragmentSettings {
            val fragment = FragmentSettings()
            fragment.activity = activity
            return fragment
        }
    }

}