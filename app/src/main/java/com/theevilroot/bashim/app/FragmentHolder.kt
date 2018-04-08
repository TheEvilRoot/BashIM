package com.theevilroot.bashim.app

import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment

class FragmentHolder(val id: Int, @IdRes val navigation: Int, val fragment: Fragment,@StringRes val title: Int)