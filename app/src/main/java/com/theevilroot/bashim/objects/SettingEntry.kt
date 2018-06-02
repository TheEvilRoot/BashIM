package com.theevilroot.bashim.objects

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class SettingEntry(@StringRes val title: Int, @DrawableRes val icon: Int?,val arrow: Boolean ,val action: (SettingEntry, View) -> Unit, val prepareView: (View, ImageView, TextView, ImageView) -> Unit = { _, _, _, _ -> })