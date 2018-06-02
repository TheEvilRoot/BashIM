package com.theevilroot.bashim.objects

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.theevilroot.bashim.R
import com.theevilroot.bashim.utils.bindView

class SettingEntryHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val icon: ImageView by bindView(R.id.setting_entry_icon)
    private val title: TextView by bindView(R.id.setting_entry_title)
    private val arrow: ImageView by bindView(R.id.setting_entry_arrow)

    fun bind(entry: SettingEntry) {
        if(entry.icon != null)
            icon.setImageDrawable(itemView.context.resources.getDrawable(entry.icon))
        else
            icon.visibility = View.GONE
        title.text = itemView.context.getString(entry.title)
        arrow.visibility = if(entry.arrow) View.VISIBLE else View.GONE
        icon.setColorFilter(itemView.context.resources.getColor(R.color.colorIcon))
        entry.prepareView(itemView, icon, title, arrow)
        itemView.setOnClickListener {
            entry.action.invoke(entry, itemView)
        }
    }

}