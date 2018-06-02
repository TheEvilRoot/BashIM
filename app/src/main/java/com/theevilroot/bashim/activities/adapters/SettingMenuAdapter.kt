package com.theevilroot.bashim.activities.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.theevilroot.bashim.R
import com.theevilroot.bashim.objects.SettingEntry
import com.theevilroot.bashim.objects.SettingEntryHolder

class SettingMenuAdapter: RecyclerView.Adapter<SettingEntryHolder>() {

    private var entries: List<SettingEntry> = emptyList()

    fun setEntries(e: List<SettingEntry>) {
        this.entries = e
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingEntryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_setting_entry, parent, false)
        return SettingEntryHolder(view)
    }

    override fun onBindViewHolder(holder: SettingEntryHolder, position: Int) {
        holder.bind(entries[position])
    }
    override fun getItemCount(): Int = entries.count()
}