package com.theevilroot.bashim.utils

import android.os.Bundle
import com.theevilroot.bashim.objects.IQuote

interface CallbackListener {

    enum class CallbackType {
        NEW_QUOTE,
        REMOVE_QUOTE,
        HIGHLIGHT_QUOTE,
        LOADING_STARTED,
        LOADING_STOPPED,
        LOADING_ERROR
    }

    fun callback(type: CallbackType, data: Bundle, quote: IQuote? = null)
}